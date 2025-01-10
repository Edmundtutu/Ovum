package com.pac.ovum.ui.cycles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.pac.ovum.R;
import com.pac.ovum.data.models.CycleSummary;

import java.util.List;

public class CyclesAdapter extends RecyclerView.Adapter<CyclesAdapter.CycleViewHolder> {

    private final List<CycleSummary> cycleSummaries; // The list of cycle data.
    private final OnCycleItemClickListener listener; // Interface for click handling.

    // Constructor
    public CyclesAdapter(List<CycleSummary> cycleSummaries, OnCycleItemClickListener listener) {
        this.cycleSummaries = cycleSummaries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new CycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CycleViewHolder holder, int position) {
        CycleSummary cycle = cycleSummaries.get(position);

        // Bind the data to views
        holder.cycleTitle.setText(cycle.getTitle());
        holder.logTextView1.setText(cycle.getCycleStatus());
        holder.logTextView2.setText(String.valueOf(cycle.getEpisodesCount())); //TODO: Replace with the computed/retrived type of Period flow from the Period-Type episode
        holder.logTextView3.setText(cycle.getSeverity());
        holder.rateTextView.setText(String.valueOf(cycle.getRating()));
        holder.severityRatingBar.setRating(cycle.getRating());

        // Handle optional data
        if (cycle.getCycleStatus() == null) {
            holder.logTextView1.setVisibility(View.GONE);
        } else {
            holder.logTextView2.setVisibility(View.VISIBLE);
        }

        // Expand button click
        holder.expandButton.setOnClickListener(v -> listener.onExpandClick(cycle));

        // Set up click listener for the entire card
        holder.itemView.setOnClickListener(v -> listener.onItemClick(cycle));
    }

    @Override
    public int getItemCount() {
        return cycleSummaries.size();
    }

    // ViewHolder class
    static class CycleViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView cycleTitle, logTextView1, logTextView2, logTextView3, rateTextView;
        RatingBar severityRatingBar;
        ImageButton expandButton;

        public CycleViewHolder(@NonNull View itemView) {
            super(itemView);

            cycleTitle = itemView.findViewById(R.id.cycleTitle);
            logTextView1 = itemView.findViewById(R.id.logTextView1);
            logTextView2 = itemView.findViewById(R.id.logTextView2);
            logTextView3 = itemView.findViewById(R.id.logTextView3);
            rateTextView = itemView.findViewById(R.id.rateTextView);
            severityRatingBar = itemView.findViewById(R.id.severityRatingBar);
            expandButton = itemView.findViewById(R.id.expandButton);
        }
    }

    // Interface for click events
    public interface OnCycleItemClickListener {
        void onItemClick(CycleSummary cycle);
        void onExpandClick(CycleSummary cycle);
    }
}

