package com.example.ovum;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BubblesListAdapter extends RecyclerView.Adapter<BubblesListAdapter.BubblesListViewHolder> {

    private List<String> childItems;

    public BubblesListAdapter(List<String> childItems) {
        this.childItems = childItems;
    }

    @NonNull
    @Override
    public BubblesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new BubblesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BubblesListViewHolder holder, int position) {
        String childItem = childItems.get(position);
        holder.textView.setText(childItem);
    }

    @Override
    public int getItemCount() {
        return childItems.size();
    }

    public static class BubblesListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public BubblesListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
