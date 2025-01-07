package com.pac.ovum.ui.home;

import static com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils.selectedDate;

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
import com.pac.ovum.utils.data.calendarutils.HorizontalCalendarUtils;
import com.pac.ovum.utils.ui.BalloonUtil;
import com.pac.ovum.utils.ui.BubblesListAdapter;
import com.pac.ovum.utils.ui.DoubleClickListener;
import com.skydoves.balloon.Balloon;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder>{
    private final ArrayList<LocalDate> datesList;
    private final AdapterView.OnItemClickListener listener;
    private final Context context;
    private final  HomeViewModel viewModel;

    public HorizontalCalendarAdapter(ArrayList<LocalDate> datesList, AdapterView.OnItemClickListener listener, Context context, HomeViewModel viewModel){
        this.datesList = datesList;
        this.listener = listener;
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public HorizontalCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each date
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new HorizontalCalendarViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HorizontalCalendarViewHolder holder, int position) {
        final LocalDate date = datesList.get(position);
        if(date != null){
            updateDateViews(holder, date);
            setDateBackground(holder, date);
            setupBalloon(holder, position);
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

    private void setDateBackground(HorizontalCalendarViewHolder holder, LocalDate date) {
        // TODO:  Logic to set background based on due dates and events
        //  Use helper methods to reduce redundancy
        if(date.equals(selectedDate)){
            holder.dayTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfWeek);
            holder.dateTextView.setBackground(holder.dateTextView.getContext().getDrawable(R.drawable.circle_background_horizontal_cal));
            holder.dateTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfMonth);
        }
    }

    private void setupEventObserver(HorizontalCalendarViewHolder holder, LocalDate date) {
        // TODO: Logic to observe events for the date
        // Nested Recycler View Logic Here
    }

    private void setupBalloon(HorizontalCalendarViewHolder holder, int position) {
        // Create the balloon using the utility class
        Balloon balloon = BalloonUtil.createBalloonForHorizontalCalendar(holder.itemView.getContext());

        holder.dateTextView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                listener.onItemClick(null, v, position, 0);
//                balloon.showAlignBottom(holder.dateTextView);
//                RecyclerView listOfShortEvents = balloon.getContentView().findViewById(R.id.bubble_recycle_view);
//                listOfShortEvents.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
//                listOfShortEvents.setAdapter(new BubblesListAdapter(eventsInShort));

                // Set the selected date in the ViewModel
                viewModel.setSelectedDate(selectedDate);

                // Observe the LiveData for events
                viewModel.getEventsForSelectedDate().observe((LifecycleOwner) context, events -> {
                    balloon.showAlignBottom(holder.dateTextView);

                    // Set up RecyclerView for events
                    RecyclerView listOfShortEvents = balloon.getContentView().findViewById(R.id.bubble_recycle_view);
                    listOfShortEvents.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                    listOfShortEvents.setAdapter(new BubblesListAdapter(events));
                });
            }

            @Override
            public void onDoubleClick(View v) {
                listener.onItemClick(null, v, position, 0);
                balloon.dismiss();
            }
        });
    }

    static class HorizontalCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView dayTextView;  // day of the week

        public HorizontalCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day_text);
            dateTextView = itemView.findViewById(R.id.date_text);
        }
    }
}
