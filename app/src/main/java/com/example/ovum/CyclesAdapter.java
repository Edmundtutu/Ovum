package com.example.ovum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CyclesAdapter extends RecyclerView.Adapter<CyclesAdapter.CycleViewHolder> {

    private List<Cycle> cycles;

    public CyclesAdapter() {
        // Static data for demonstration
        cycles = new ArrayList<>();
        cycles.add(new Cycle("Cycle Five", "30th June, 2024", "Medium FLo", "Not Severe", 4.0f));
        cycles.add(new Cycle("Cycle Four", "29th May, 2024", "Light Flo", "Ideal Health", 3.0f));
        cycles.add(new Cycle("Cycle Three", "25th April, 2024", "Heavy Flo", "Severe, Consider Diagnosis", 5));
        cycles.add(new Cycle("Cycle Two", "20th March, 2024", "Medium Flo", "Not Severe", 4.5f));
        cycles.add(new Cycle("Cycle One", "15th February, 2024", "Light Flo", "Ideal Health", 2.7f));
        // Add more cycles as needed
    }

    @NonNull
    @Override
    public CycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CycleViewHolder holder, int position) {
        Cycle cycle = cycles.get(position);
        holder.cycleTitle.setText(cycle.getCycleTitle());
        holder.logTextView1.setText(cycle.getLog1());
        holder.logTextView2.setText(cycle.getLog2());
        holder.logTextView3.setText(cycle.getLog3());
        holder.severityRatingBar.setRating(cycle.getRating());
        holder.ratingTextView.setText((String.valueOf(cycle.getRating())));
        holder.cardView.setCardBackgroundColor(cycle.getbackgroundColor());
    }

    @Override
    public int getItemCount() {
        return cycles.size();
    }

    public static class CycleViewHolder extends RecyclerView.ViewHolder {

        TextView cycleTitle;
        ImageButton expandButton;
        TextView logTextView1;
        TextView logTextView2;
        TextView logTextView3;
        TextView ratingTextView;
        RatingBar severityRatingBar;
        CardView cardView;

        public CycleViewHolder(@NonNull View itemView) {
            super(itemView);
            cycleTitle = itemView.findViewById(R.id.cycleTitle);
            expandButton = itemView.findViewById(R.id.expandButton);
            logTextView1 = itemView.findViewById(R.id.logTextView1);
            logTextView2 = itemView.findViewById(R.id.logTextView2);
            logTextView3 = itemView.findViewById(R.id.logTextView3);
            severityRatingBar = itemView.findViewById(R.id.severityRatingBar);
            ratingTextView = itemView.findViewById(R.id.rateTextView);
            cardView = itemView.findViewById(R.id.cardView);

            // You can add a click listener to expandButton if needed
            // expandButton.setOnClickListener(view -> { /* Handle expand action */ });
        }
    }
}
