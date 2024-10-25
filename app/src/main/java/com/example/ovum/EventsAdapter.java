package com.example.ovum;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private List<Event> events;

    public EventsAdapter() {
        events = new ArrayList<>();
        DateUtils dateUtils = new DateUtils();
        events.add(new Event(Color.BLUE, dateUtils.convertToMilliseconds("2024,08,16"), "Meet Doctor"));
        events.add(new Event(Color.BLUE, dateUtils.convertToMilliseconds("2024,08,16"), "Take a Pill"));
        events.add(new Event(Color.BLUE, dateUtils.convertToMilliseconds("2024,08,16"), "Use Drug"));
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_view, parent, false);
        return new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        Event event = events.get(position);
        String[] eventData = getEventData(event);

        // Bind extracted event details to the views
        holder.event.setText(eventData[2]); // Event title
        holder.eventDayOfWeek.setText(eventData[0]); // Day of the week (e.g., Mon, Tue)
        holder.eventDateOfWeek.setText(eventData[1]); // Date of the event
        holder.eventFullDate.setText(eventData[3]); // Full date
        holder.eventTimeStamp.setText(eventData[4]); // Event timestamp
    }

    // Method to extract event details from the event
    private String[] getEventData(Event event) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        long eventTimeInMillis = event.getTimeInMillis();
        String dayOfWeekShort = dayFormat.format(eventTimeInMillis); // Mon, Tue
        String dateForWeek = dateFormat.format(eventTimeInMillis); // 16, 17, etc.
        String eventContent = (String) event.getData(); // Event title (e.g., "Meet Doctor")
        String fullDate = fullDateFormat.format(eventTimeInMillis); // 2024-08-16
        String timeStamp = timeStampFormat.format(eventTimeInMillis); // Time of event

        return new String[]{dayOfWeekShort, dateForWeek, eventContent, fullDate, timeStamp};
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        // Define the views for each item
        TextView eventDayOfWeek;
        TextView eventDateOfWeek;
        TextView eventFullDate;
        TextView event;
        TextView eventTimeStamp;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.event_title);
            eventDateOfWeek = itemView.findViewById(R.id.day_of_month);
            eventDayOfWeek = itemView.findViewById(R.id.day_of_week);
            eventFullDate = itemView.findViewById(R.id.event_date);
            eventTimeStamp = itemView.findViewById(R.id.time_stamp);
        }
    }
}
