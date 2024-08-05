package com.example.ovum;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder> {

    private final ArrayList<LocalDate> datesList;
    private final AdapterView.OnItemClickListener listener;

    // Constructor for the adapter
    public HorizontalCalendarAdapter(ArrayList<LocalDate> datesList, AdapterView.OnItemClickListener listener) {
        this.datesList = datesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HorizontalCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each date
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new HorizontalCalendarViewHolder(view);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HorizontalCalendarViewHolder holder, int position) {
        final LocalDate date =datesList.get(position);
        if(date != null){
            holder.dayTextView.setText(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()));
            holder.dateTextView.setText(String.valueOf(date.getDayOfMonth()));
            holder.dateTextView.setOnClickListener(v -> {
                listener.onItemClick(null, v, position, 0);
            });
            if(date.equals(CalendarUtils.selectedDate)){
                holder.dateTextView.setBackground(holder.dateTextView.getContext().getDrawable(R.drawable.circle_background_horizontal_cal));
                holder.dateTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfMonth);
            }
            // Highlight the due date and its associate dates
            // get the DueDate associate dates
            ArrayList<LocalDate> dueDateAssociates = CalendarUtils.dueDateAssociates;
            if(dueDateAssociates != null){
                // set the background color of the due date and its associate dates
                LocalDate dueDateObj = dueDateAssociates.get(0);
                LocalDate oneToDueDate = dueDateAssociates.get(1);
                LocalDate twoToDueDate = dueDateAssociates.get(2);
                LocalDate oneFromDueDate = dueDateAssociates.get(3);
                LocalDate twoFromDueDate = dueDateAssociates.get(4);
                if(date.equals(dueDateObj)){
                    // set the background resource of the due date
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.due_date_background));
                } else if (date.equals(oneToDueDate)) {
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.one_day_to_due_date_background));
                } else if (date.equals(twoToDueDate)) {
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.two_days_to_due_date_background));
                } else if (date.equals(oneFromDueDate)) {
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.one_day_from_due_date_background));
                } else if (date.equals(twoFromDueDate)) {
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.two_days_from_due_date_background));
                }


            }

            // Highlight the events of the day
            // get the events of the day
            HashMap<LocalDate, String> eventsOfTheDay = CalendarUtils.eventsOfTheDay;
            if(eventsOfTheDay != null){
                // set the background color of a date with an event
                 LocalDate eventDate = CalendarUtils.eventsOfTheDay.keySet().iterator().next();
                if(date.equals(eventDate)){
                    // set the background resource of the due date
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.event_background));
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return datesList.size();
    }

    // ViewHolder class for each date item
    static class HorizontalCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView dayTextView;  // day of the week

        public HorizontalCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day_text);
            dateTextView = itemView.findViewById(R.id.date_text);
        }
    }

    // Interface for handling date selection
    public interface OnItemListener {
        void onItemCLick(int position, LocalDate date);
    }
}
