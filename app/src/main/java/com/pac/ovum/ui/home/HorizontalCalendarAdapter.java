package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.selectedDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.utils.ui.BalloonUtil;
import com.pac.ovum.utils.ui.BubblesListAdapter;
import com.pac.ovum.utils.ui.DoubleClickListener;
import com.skydoves.balloon.Balloon;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder> {
    private final ArrayList<LocalDate> datesList;
    private final AdapterView.OnItemClickListener listener;
    private final Context context;
    private final HomeViewModel viewModel;
    private Boolean hasEvent = false;
    private Balloon currentBalloon; // Track current balloon
    private int selectedPosition = -1; // Track selected position

    public HorizontalCalendarAdapter(ArrayList<LocalDate> datesList, AdapterView.OnItemClickListener listener, Context context, HomeViewModel viewModel) {
        this.datesList = datesList;
        this.listener = listener;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public HorizontalCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new HorizontalCalendarViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HorizontalCalendarViewHolder holder, int position) {
        final LocalDate date = datesList.get(position);
        if (date != null) {
            updateDateViews(holder, date);
            setDateBackground(holder, date);
            setupDateClickListener(holder, position, date);
        }
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDateViews(HorizontalCalendarViewHolder holder, LocalDate date) {
        holder.dayTextView.setText(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()));
        holder.dateTextView.setText(String.valueOf(date.getDayOfMonth()));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDateBackground(HorizontalCalendarViewHolder holder, LocalDate date) {
        // TODO:  Logic to set background based on due dates and events
        //  Use helper methods to reduce redundancy
        if(date.equals(selectedDate)){
            holder.dayTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfWeek);
            holder.dateTextView.setBackground(holder.dateTextView.getContext().getDrawable(R.drawable.circle_background_horizontal_cal));
            holder.dateTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfMonth);
        }

        if(hasEvent){
            holder.dateTextView.setBackground(holder.dateTextView.getContext().getDrawable(R.drawable.event_background));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupDateClickListener(HorizontalCalendarViewHolder holder, int position, LocalDate date) {
        holder.itemView.setOnClickListener(new DoubleClickListener(200) {
            @Override
            public void onSingleClick(View v) {
                handleSingleClick(holder, position, date);
            }

            @Override
            public void onDoubleClick(View v) {
                handleDoubleClick(position);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleSingleClick(HorizontalCalendarViewHolder holder, int position, LocalDate date) {
        // Dismiss previous balloon if exists
        dismissCurrentBalloon();

        // Update selection
        int previousSelected = selectedPosition;
        selectedPosition = position;
        selectedDate = date;
        
        // Notify adapter of changes
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);

        // Notify listener
        listener.onItemClick(null, holder.itemView, position, 0);

        // Show balloon with events
        showBalloonWithEvents(holder, date);
    }

    private void handleDoubleClick(int position) {
        dismissCurrentBalloon();
        listener.onItemClick(null, null, position, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showBalloonWithEvents(HorizontalCalendarViewHolder holder, LocalDate date) {
        currentBalloon = BalloonUtil.createBalloonForHorizontalCalendar(holder.itemView.getContext());
        
        viewModel.setSelectedDate(date);
        viewModel.getEventsForSelectedDate().observe((LifecycleOwner) context, events -> {
            if (events != null && !events.isEmpty()) {
                // flag the date with color by assigning the hasEvent to true
                hasEvent = !hasEvent? true: true;

                RecyclerView listOfShortEvents = currentBalloon.getContentView()
                        .findViewById(R.id.bubble_recycle_view);
                listOfShortEvents.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                listOfShortEvents.setAdapter(new BubblesListAdapter(events));
                currentBalloon.showAlignBottom(holder.dateTextView);
            }else {
                hasEvent = false;
            }
        });
    }

    private void dismissCurrentBalloon() {
        if (currentBalloon != null && currentBalloon.isShowing()) {
            currentBalloon.dismiss();
            currentBalloon = null;
        }
    }

    static class HorizontalCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView dayTextView;

        public HorizontalCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day_text);
            dateTextView = itemView.findViewById(R.id.date_text);
        }
    }
}

