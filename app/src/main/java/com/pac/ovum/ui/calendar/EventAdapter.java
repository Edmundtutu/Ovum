package com.pac.ovum.ui.calendar;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.data.models.Event;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events = new ArrayList<>();
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("EEE");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_view, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView dayOfWeek;
        private final TextView dayOfMonth;
        private final TextView eventTitle;
        private final TextView timeStamp;
        private final TextView eventDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.day_of_week);
            dayOfMonth = itemView.findViewById(R.id.day_of_month);
            eventTitle = itemView.findViewById(R.id.event_title);
            timeStamp = itemView.findViewById(R.id.time_stamp);
            eventDate = itemView.findViewById(R.id.event_date);
        }

        public void bind(Event event) {
            dayOfWeek.setText(event.getEventDate().format(DAY_FORMATTER));
            dayOfMonth.setText(String.valueOf(event.getEventDate().getDayOfMonth()));
            eventTitle.setText(event.getEventType());
            
            if (event.getEventTime() != null) {
                timeStamp.setText(event.getEventTime().format(TIME_FORMATTER));
            } else {
                timeStamp.setText("--:--");
            }
            
            eventDate.setText(event.getEventDate().toString());
        }
    }
} 