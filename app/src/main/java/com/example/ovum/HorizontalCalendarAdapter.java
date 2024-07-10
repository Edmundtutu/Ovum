package com.example.ovum;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class HorizontalCalendarAdapter extends PagingDataAdapter<LocalDate, HorizontalCalendarAdapter.CalendarViewHolder> {

    private int selectedPosition = RecyclerView.NO_POSITION;

    private OnItemClickListener listener;
    private Set<LocalDate> selectedDates = new HashSet<>();
    private Set<LocalDate> dueDates = new HashSet<>();
    private Set<LocalDate> oneFromDueDates = new HashSet<>();
    private Set<LocalDate> oneToDueDates = new HashSet<>();
    private Set<LocalDate> twoFromDueDates = new HashSet<>();
    private Set<LocalDate> twoToDueDates = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(LocalDate date);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public HorizontalCalendarAdapter(@NonNull DiffUtil.ItemCallback<LocalDate> diffCallback) {
        super(diffCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = getItem(position);
        if (date != null) {
            boolean isSelected = date.isEqual(LocalDate.now());
            boolean isDueDate = dueDates != null && dueDates.contains(date);
            boolean isOneFromDueDate = oneFromDueDates != null && oneFromDueDates.contains(date);
            boolean isOneToDueDate = oneToDueDates != null && oneToDueDates.contains(date);
            boolean isTwoFromDueDate = twoFromDueDates != null && twoFromDueDates.contains(date);
            boolean isTwoToDueDate = twoToDueDates != null && twoToDueDates.contains(date);
            Log.d(TAG, "Binding date: " + date + ", isSelected: " + isSelected + ", isDueDate: " + isDueDate + ", isOnefromDuedate:" + isOneFromDueDate + ", isTwoFromDuedate" + isTwoFromDueDate + ", isOneToDuedate" + isOneToDueDate + ", isTwoToDueDate" + isTwoToDueDate);
            holder.bind(date, isSelected, isDueDate, isOneFromDueDate, isOneToDueDate, isTwoFromDueDate, isTwoToDueDate);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(date);
                    // Update selection state
                    selectedDates.clear();
                    selectedDates.add(date);
                    notifyDataSetChanged();
                }
            });
        }
//        boolean isSelected = selectedDates.contains(date);
//        boolean isDueDate = dueDates.contains(date);
//        boolean isOneFromDueDate = oneFromDueDates.contains(date);
//        boolean isOneToDueDate = oneToDueDates.contains(date);
//        boolean isTwoFromDueDate = twoFromDueDates.contains(date);
//        boolean isTwoToDueDate = twoToDueDates.contains(date);
//        if (date != null) {
//            holder.bind(date, isSelected, isDueDate, isOneFromDueDate, isOneToDueDate, isTwoFromDueDate, isTwoToDueDate);
//            holder.itemView.setOnClickListener(v -> {
//                if (listener != null) {
//                    listener.onItemClick(date);
//                    // Update selection state
//                    selectedDates.clear();
//                    selectedDates.add(date);
//                    notifyDataSetChanged();
//                }
//            });
//        }
//        if (date != null) {
//            holder.bind(date, position == selectedPosition);
//        }
//
//        holder.itemView.setOnClickListener(v -> {
//            notifyItemChanged(selectedPosition);
//            selectedPosition = holder.getBindingAdapterPosition();
//            notifyItemChanged(selectedPosition);
//
//            // Notify the activity or fragment about the selected date
//            if (onItemClickListener != null) {
//                onItemClickListener.onItemClick(date);
//            }
//        });
    }

//    private OnItemClickListener onItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.onItemClickListener = listener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(LocalDate date);
//    }
    public void setDueDates(Set<LocalDate> dueDates) {
        this.dueDates = dueDates;
        Log.d(TAG, "Adapter due dates set: " + dueDates);
        notifyDataSetChanged();
    }

    public void setOneFromDueDates(Set<LocalDate> oneFromDueDates) {
        this.oneFromDueDates = oneFromDueDates;
        Log.d(TAG, "Adapter oneFromDueDates set: " + oneFromDueDates);
        notifyDataSetChanged();
    }

    public void setOneToDueDates(Set<LocalDate> oneToDueDates) {
        this.oneToDueDates = oneToDueDates;
        Log.d(TAG, "Adapter oneToDueDates set: " + oneToDueDates);
        notifyDataSetChanged();
    }

    public void setTwoFromDueDates(Set<LocalDate> twoFromDueDates) {
        this.twoFromDueDates = twoFromDueDates;
        Log.d(TAG, "Adapter twoFromDueDates set: " + twoFromDueDates);
        notifyDataSetChanged();
    }

    public void setTwoToDueDates(Set<LocalDate> twoToDueDates) {
        this.twoToDueDates = twoToDueDates;
        Log.d(TAG, "Adapter twoToDueDates set: " + twoToDueDates);
        notifyDataSetChanged();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {

        private final TextView dayOfWeekTextView;
        private final TextView dayOfMonthTextView;
        private final DateTimeFormatter dayOfWeekFormatter;
        private final DateTimeFormatter dayOfMonthFormatter;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextView = itemView.findViewById(R.id.day_of_week);
            dayOfMonthTextView = itemView.findViewById(R.id.day_of_month);
            dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE");
            dayOfMonthFormatter = DateTimeFormatter.ofPattern("d");
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(LocalDate date, boolean isSelected, boolean isDueDate,
                         boolean isOneFromDueDate, boolean isOneToDueDate,
                         boolean isTwoFromDueDate, boolean isTwoToDueDate) {
            dayOfWeekTextView.setText(dayOfWeekFormatter.format(date));
            dayOfMonthTextView.setText(dayOfMonthFormatter.format(date));

            // for viewing in action and debug lets first make these values true and see want we want
//            isDueDate = true;
//            isOneFromDueDate = true;
//            isOneToDueDate = true;
//            isTwoFromDueDate = true;
//            isTwoToDueDate = true;
            int backgroundResource = 0;

            if (isSelected) {
                backgroundResource = R.drawable.circle_background_enable;
            } else if (isDueDate) {
                backgroundResource = R.drawable.background_is_due_date;
            } else if (isOneFromDueDate) {
                backgroundResource = R.drawable.background_is_1_from_due_date;
            } else if (isOneToDueDate) {
                backgroundResource = R.drawable.background_is_1_to_due_date;
            } else if (isTwoFromDueDate) {
                backgroundResource = R.drawable.background_is_2_from_due_date;
            } else if (isTwoToDueDate) {
                backgroundResource = R.drawable.background_is_2_to_due_date;
            }

            itemView.setBackgroundResource(backgroundResource);
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<LocalDate> {
        @Override
        public boolean areItemsTheSame(@NonNull LocalDate oldItem, @NonNull LocalDate newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull LocalDate oldItem, @NonNull LocalDate newItem) {
            return oldItem.equals(newItem);
        }
    }
}
