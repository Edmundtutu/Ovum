package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.selectedDate;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.data.models.Event;
import com.pac.ovum.utils.ui.BalloonUtil;
import com.pac.ovum.utils.ui.BubblesListAdapter;
import com.pac.ovum.utils.ui.DoubleClickListener;
import com.skydoves.balloon.Balloon;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder> {
    private final ArrayList<LocalDate> datesList;
    private final OnDateSelectedListener listener;
    private final Context context;
    private final HomeViewModel viewModel;
    private Balloon currentBalloon;
    private int selectedPosition = -1;
    private Map<LocalDate, Boolean> dateHasEvents = new HashMap<>(); // Cache for dates with events

    public interface OnDateSelectedListener {
        void onDateSelected(LocalDate date);
    }

    public HorizontalCalendarAdapter(ArrayList<LocalDate> datesList, OnDateSelectedListener listener, Context context, HomeViewModel viewModel) {
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
        LocalDate date = datesList.get(position);
        if (date != null) {
            holder.dayTextView.setText(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()));
            holder.dateTextView.setText(String.valueOf(date.getDayOfMonth()));
            
            // Update background based on selection and events
            updateDateItemBackground(holder, date, position);
            
            holder.itemView.setOnClickListener(new DoubleClickListener(200) {
                @Override
                public void onSingleClick(View v) {
                    int previousSelected = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(previousSelected);
                    notifyItemChanged(selectedPosition);
                    handleSingleClick(holder, date);
                }

                @Override
                public void onDoubleClick(View v) {
                    // Handle double click if needed
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDateItemBackground(HorizontalCalendarViewHolder holder, LocalDate date, int position) {
        boolean hasEvents = dateHasEvents.containsKey(date);
        
        if (position == selectedPosition || date.equals(selectedDate)) {
            // Selected date background
            holder.dateTextView.setBackground(
                ContextCompat.getDrawable(context, R.drawable.selected_background)
            );
            holder.dayTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.black)
            );
        } else if (hasEvents) {
            // Date with events background
            holder.dateTextView.setBackground(
                ContextCompat.getDrawable(context, R.drawable.event_background)
            );
            holder.dayTextView.setTextColor(
                ContextCompat.getColor(context, R.color.green_50)
            );
        } else {
            // Default background
            holder.dateTextView.setBackground(null);
            holder.dayTextView.setTextColor(
                ContextCompat.getColor(context, android.R.color.darker_gray)
            );
        }
    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleSingleClick(HorizontalCalendarViewHolder holder, LocalDate date) {
        dismissCurrentBalloon();
        selectedDate = date;
        listener.onDateSelected(date);
        showBalloonWithEvents(holder, date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showBalloonWithEvents(HorizontalCalendarViewHolder holder, LocalDate date) {
        Log.d("HorizontalCalendarAdapter", "Attempting to show balloon for date: " + date);
        
        // Check if context is valid and activity is not finishing
        if (context == null || !(context instanceof FragmentActivity) || 
            ((FragmentActivity) context).isFinishing() || 
            ((FragmentActivity) context).isDestroyed()) {
            Log.d("HorizontalCalendarAdapter", "Context is invalid or activity is finishing/destroyed");
            return;
        }

        try {
            currentBalloon = BalloonUtil.createBalloonForHorizontalCalendar(holder.itemView.getContext());
            Log.d("HorizontalCalendarAdapter", "Balloon created");
            
            viewModel.setSelectedDate(date);
            viewModel.getEventsForSelectedDate().observe((LifecycleOwner) context, events -> {
                // Check again if context is still valid before showing balloon
                if (context == null || !(context instanceof FragmentActivity) || 
                    ((FragmentActivity) context).isFinishing() || 
                    ((FragmentActivity) context).isDestroyed() || 
                    currentBalloon == null) {
                    Log.d("HorizontalCalendarAdapter", "Context became invalid while loading events");
                    return;
                }

                if (events != null && !events.isEmpty()) {
                    try {
                        RecyclerView listOfShortEvents = currentBalloon.getContentView()
                                .findViewById(R.id.bubble_recycle_view);
                        listOfShortEvents.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                        listOfShortEvents.setAdapter(new BubblesListAdapter(events));
                        
                        // Check view validity before showing balloon
                        if (holder.dateTextView.getWindowToken() != null) {
                            currentBalloon.showAlignBottom(holder.dateTextView);
                            Log.d("HorizontalCalendarAdapter", "Balloon shown for date: " + date);
                        } else {
                            Log.d("HorizontalCalendarAdapter", "View is no longer valid");
                        }
                    } catch (Exception e) {
                        Log.e("HorizontalCalendarAdapter", "Error showing balloon", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("HorizontalCalendarAdapter", "Error creating balloon", e);
        }
    }

    public void dismissCurrentBalloon() {
        try {
            if (currentBalloon != null && currentBalloon.isShowing()) {
                currentBalloon.dismiss();
                Log.d("HorizontalCalendarAdapter", "Balloon dismissed");
            }
        } catch (Exception e) {
            Log.e("HorizontalCalendarAdapter", "Error dismissing balloon", e);
        } finally {
            currentBalloon = null;
        }
    }

    // Add method to cleanup resources
    public void cleanup() {
        dismissCurrentBalloon();
        if (context instanceof LifecycleOwner) {
            viewModel.getEventsForSelectedDate().removeObservers((LifecycleOwner) context);
        }
    }

    // Add method to set initial selected date
    public void setSelectedDate(LocalDate date) {
        int position = datesList.indexOf(date);
        if (position != -1) {
            selectedPosition = position;
            selectedDate = date;
            notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEvents(List<Event> events) {
        // Clear previous cache
        dateHasEvents.clear();
        
        // Cache which dates have events
        if (events != null) {
            for (Event event : events) {
                dateHasEvents.put(event.getEventDate(), true);
            }
        }
        notifyDataSetChanged();
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

