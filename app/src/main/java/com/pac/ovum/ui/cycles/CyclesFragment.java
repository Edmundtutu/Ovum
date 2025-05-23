package com.pac.ovum.ui.cycles;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.pac.ovum.R;
import com.pac.ovum.data.models.CycleSummary;
import com.pac.ovum.data.repositories.CycleRepository;
import com.pac.ovum.data.repositories.EpisodeRepository;
import com.pac.ovum.data.repositories.MockCycleRepository;
import com.pac.ovum.data.repositories.MockEpisodesRepository;
import com.pac.ovum.databinding.FragmentCyclesBinding;
import com.pac.ovum.utils.AppModule;

import java.util.ArrayList;
import java.util.List;

public class CyclesFragment extends Fragment {

    private FragmentCyclesBinding binding;
    private CyclesViewModel cyclesViewModel;
    private CyclesAdapter cyclesAdapter;
    private LineChart lineChart;
    private NestedScrollView scrollView;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    // Observer for cycle summaries to prevent memory leaks
    private Observer<List<CycleSummary>> cyclesObserver;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCyclesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the repository and ViewModel
        CycleRepository cycleRepository = AppModule.provideCycleRepository(getContext());
        EpisodeRepository episodeRepository = AppModule.provideEpisodeRepository(getContext());
        cyclesViewModel = new ViewModelProvider(this, new CyclesViewModelFactory(cycleRepository, episodeRepository)).get(CyclesViewModel.class);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = binding.swipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(() -> syncDataFromApi());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.purple_500,
                R.color.teal_200,
                R.color.purple_700);

        // Initialize LineChart and ScrollView
        lineChart = binding.chart;
        scrollView = binding.mainScrollView;
        setupChart();
        
        // Set up RecyclerView
        RecyclerView recyclerView = binding.cardRecyleView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Set up sync button
        binding.syncButton.setOnClickListener(v -> syncDataToApi());

        // Observe sync status
        observeSyncStatus();

        // Remove previous observer if exists to prevent leaks
        if (cyclesObserver != null) {
            cyclesViewModel.getCycleSummaries(getCurrentUserId()).removeObserver(cyclesObserver);
        }
        
        // Create new observer that properly cleans up resources
        cyclesObserver = cycleSummaries -> {
            cyclesAdapter = new CyclesAdapter(cycleSummaries, new CyclesAdapter.OnCycleItemClickListener() {
                @Override
                public void onItemClick(CycleSummary cycle) {
                    // Smooth scroll to chart before updating
                    scrollView.smoothScrollTo(0, lineChart.getTop());
                    
                    // Update the graph with the selected cycle's data after a short delay
                    lineChart.postDelayed(() -> {
                        updateGraphs(cycleSummaries, cycle);
                    }, 300); // Small delay to ensure smooth scroll completes
                }

                @Override
                public void onExpandClick(CycleSummary cycle) {
                    // Handle expand click
                    // cycle.setExpanded(!cycle.isExpanded());
                    // cyclesAdapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(cyclesAdapter);
            
            // Initialize graph with the first cycle if available
            if (!cycleSummaries.isEmpty()) {
                updateGraphs(cycleSummaries, cycleSummaries.get(0));
            }
        };
        
        // Observe cycles data with our managed observer
        cyclesViewModel.getCycleSummaries(getCurrentUserId())
            .observe(getViewLifecycleOwner(), cyclesObserver);
        
        return root;
    }
    
    private void observeSyncStatus() {
        // Observe syncing status
        cyclesViewModel.getIsSyncing().observe(getViewLifecycleOwner(), isSyncing -> {
            swipeRefreshLayout.setRefreshing(isSyncing);
            binding.syncButton.setEnabled(!isSyncing);
            
            if (isSyncing) {
                binding.syncButton.setText(R.string.syncing);
            } else {
                binding.syncButton.setText(R.string.sync_to_server);
            }
        });
        
        // Observe sync errors
        cyclesViewModel.getSyncError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", v -> {})
                        .show();
            }
        });
        
        // Observe sync success
        cyclesViewModel.getSyncSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Sync completed successfully", Toast.LENGTH_SHORT).show();
                
                // Refresh data after successful sync by re-observing
                cyclesViewModel.getCycleSummaries(getCurrentUserId())
                    .observe(getViewLifecycleOwner(), cyclesObserver);
            }
        });
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncDataFromApi() {
        cyclesViewModel.syncFromApi(getCurrentUserId());
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void syncDataToApi() {
        cyclesViewModel.syncToApi(getCurrentUserId());
    }
    
    private void setupChart() {
        // Basic chart setup
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setMaxHighlightDistance(300);
        
        // Enable animations with longer duration
        lineChart.animateX(2000, Easing.EaseInOutCubic);

        // X-axis setup
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(11f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineWidth(1f);
        xAxis.setAxisLineColor(Color.DKGRAY);
        
        // Y-axis setup
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Remove grid lines
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setTextSize(11f);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setAxisLineColor(Color.DKGRAY);
        
        // Disable right Y-axis
        lineChart.getAxisRight().setEnabled(false);
        
        // Legend setup
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);
        legend.setTextSize(12f);
        legend.setTextColor(Color.DKGRAY);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(8f);
        legend.setXEntrySpace(12f);
        legend.setYEntrySpace(6f);
        legend.setWordWrapEnabled(true);
    }
    
    private void updateGraphs(List<CycleSummary> allCycles, CycleSummary selectedCycle) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        
        // 1️⃣ Overall Episodes Graph
        List<Entry> overallEntries = new ArrayList<>();
        for (int i = 0; i < allCycles.size(); i++) {
            overallEntries.add(new Entry(i, allCycles.get(i).getEpisodesCount()));
        }

        // 2️⃣ Daily Episode Trends
        List<Entry> episodeEntries = new ArrayList<>();
        List<Integer> dailySeverities = selectedCycle.getGraphData();
        for (int j = 0; j < dailySeverities.size(); j++) {
            episodeEntries.add(new Entry(j, dailySeverities.get(j)));
        }

        // 3️⃣ Cycle Severity Graph
        List<Entry> severityEntries = new ArrayList<>();
        for (int i = 0; i < allCycles.size(); i++) {
            severityEntries.add(new Entry(i, allCycles.get(i).getRating()));
        }
        
        // Create and style the datasets with enhanced visuals
        LineDataSet overallSet = createDataSet(overallEntries, "Overall Episodes", 
            R.color.teal_700, false); // Changed to continuous line
        
        LineDataSet dailySet = createDataSet(episodeEntries, "Daily Symptoms", 
                R.color.purple_500, false);
        
        LineDataSet severitySet = createDataSet(severityEntries, "Severity", 
            android.R.color.holo_red_light, false); // Changed to continuous line

        // Add datasets to the list
        dataSets.add(overallSet);
        dataSets.add(dailySet);
        dataSets.add(severitySet);
        
        // Combine all datasets
        LineData lineData = new LineData(dataSets);
        lineData.setValueTextSize(10f);
        lineData.setValueTextColor(Color.DKGRAY);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
    
    private LineDataSet createDataSet(List<Entry> entries, String label, int colorResId, boolean isDashed) {
        LineDataSet set = new LineDataSet(entries, label);
        int color = ContextCompat.getColor(requireContext(), colorResId);

//        set.setLineWidth(2.5f); // Slightly thicker lines
//        set.setCircleColor(color);
//        set.setCircleRadius(4f);
//        set.setDrawCircleHole(true);
//        set.setCircleHoleRadius(2f);
//        set.setValueTextSize(10f);
//        set.setDrawValues(true);
        set.setColor(color);
        set.setLineWidth(2.5f);
        
        // Make circles very small or remove them
        set.setDrawCircles(false); // Remove circles completely
        set.setDrawCircleHole(false);
        
        // Value text settings
        set.setDrawValues(false); // Remove value labels for cleaner look
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        
        // Remove dashed lines
        if (isDashed) {
            set.enableDashedLine(1f, 0f, 0f);
        }
        
        // Fill area under the line with gradient
        set.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            int gradientResId;
            if (colorResId == R.color.teal_700) {
                gradientResId = R.drawable.fade_teal;
            } else if (colorResId == R.color.purple_500) {
                gradientResId = R.drawable.fade_purple;
            } else {
                gradientResId = R.drawable.fade_red;
            }
            Drawable drawable = ContextCompat.getDrawable(requireContext(), gradientResId);
            set.setFillDrawable(drawable);
        } else {
            set.setFillColor(color);
            set.setFillAlpha(50);
        }

        // Highlight customization
        set.setHighlightEnabled(true);
        set.setHighLightColor(color);
        set.setHighlightLineWidth(1.5f);
        set.setDrawHorizontalHighlightIndicator(false);
        
        return set;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Clean up observers to prevent memory leaks
        if (cyclesObserver != null && cyclesViewModel != null) {
            cyclesViewModel.getCycleSummaries(getCurrentUserId()).removeObserver(cyclesObserver);
            cyclesObserver = null;
        }
        
        binding = null;
    }
    
    private int getCurrentUserId() {
        return 1; // Placeholder
    }
}