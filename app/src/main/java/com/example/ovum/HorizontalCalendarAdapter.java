package com.example.ovum;

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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ovum.ui.DoubleClickListener;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder> {

    private final ArrayList<LocalDate> datesList;
    private final AdapterView.OnItemClickListener listener;

    private MainViewModel mainViewModel;

    private LifecycleOwner lifecycleOwner;

    private final Context context;

    // Constructor for the adapter
    public HorizontalCalendarAdapter(MainViewModel mainViewModel, ArrayList<LocalDate> datesList, AdapterView.OnItemClickListener listener,LifecycleOwner lifecycleOwner , Context context) {
        this.datesList = datesList;
        this.listener = listener;
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.mainViewModel = mainViewModel;
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
    public void onBindViewHolder(@NonNull HorizontalCalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final LocalDate date =datesList.get(position);
        if(date != null){
            holder.dayTextView.setText(date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.getDefault()));
            holder.dateTextView.setText(String.valueOf(date.getDayOfMonth()));
//            holder.dateTextView.setOnClickListener(v -> {
//                listener.onItemClick(null, v, position, 0);
//            });
            if(date.equals(CalendarUtils.selectedDate)){
                holder.dayTextView.setTextAppearance(R.style.HorizontalCalendar_Text_SelectedDayOfWeek);
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
            // instanciate the list to store the specific string values of the events of the day
            List<String> eventsInShort = new ArrayList<>();
            if(eventsOfTheDay != null){
                // decode the HashMap to extract the string values of the events of the day
                eventsInShort.addAll(eventsOfTheDay.values());

                // set the background color of a date with an event
                 LocalDate eventDate = CalendarUtils.eventsOfTheDay.keySet().iterator().next();
                if(date.equals(eventDate)){
                    // set the background resource of the due date
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.event_background));
                }
            }else {
                // add text message to the eventsInShort list to display on the balloon with the message "No events"
                eventsInShort.add("No events scheduled");
            }

            // using liveData to observe the list of events for the given date

            // Get the ViewModel instance
             if (mainViewModel != null) {
                mainViewModel.fetchEventsForDate(date);
            }

            // Observe the LiveData to track any changes in events
            mainViewModel.getEventsLiveData().observe(lifecycleOwner, eventsMap -> {
                if (eventsMap.containsKey(date)) {
                    String event = eventsMap.get(date);
                    // Update UI to show the event for the home display date
                    holder.dateTextView.setBackgroundDrawable(holder.dateTextView.getContext().getDrawable(R.drawable.event_background));
                }
            });

            // Displaying the The list of events attached on the day Using the  skyDove balloon
            Balloon balloon = createBalloonTip();

            holder.dateTextView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    listener.onItemClick(null, v, position, 0);
                    balloon.showAlignBottom(holder.dateTextView);
                    RecyclerView listOfShortEvents = balloon.getContentView().findViewById(R.id.bubble_recycle_view);
                    listOfShortEvents.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                    listOfShortEvents.setAdapter(new BubblesListAdapter(eventsInShort));
                }

                @Override
                public void onDoubleClick(View v) {
                    listener.onItemClick(null, v, position, 0);
                    balloon.dismiss();
                }
            });

        }

    }
    private Balloon createBalloonTip() {
        return new Balloon.Builder(context)
                .setLayout(R.layout.bubble)
                .setArrowPosition(0.5f)
                .setCornerRadius(8f)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
                .setLifecycleOwner(lifecycleOwner)
                .build();
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
