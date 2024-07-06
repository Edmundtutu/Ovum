package com.example.ovum;

import android.os.Build;
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

public class HorizontalCalendarAdapter extends PagingDataAdapter<LocalDate, HorizontalCalendarAdapter.CalendarViewHolder> {

    private int selectedPosition = RecyclerView.NO_POSITION;
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
            holder.bind(date, position == selectedPosition);
        }

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(selectedPosition);

            // Notify the activity or fragment about the selected date
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(date);
            }
        });
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(LocalDate date);
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
        public void bind(LocalDate date, boolean isSelected) {
            dayOfWeekTextView.setText(dayOfWeekFormatter.format(date));
            dayOfMonthTextView.setText(dayOfMonthFormatter.format(date));

            dayOfWeekTextView.setText(dayOfWeekFormatter.format(date));
            dayOfMonthTextView.setText(dayOfMonthFormatter.format(date));
            itemView.setBackgroundResource(isSelected ? R.drawable.circle_background_enable : 0);
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
