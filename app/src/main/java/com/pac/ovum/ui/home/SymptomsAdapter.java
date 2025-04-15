package com.pac.ovum.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pac.ovum.R;
import com.pac.ovum.data.models.Episode;

import java.time.format.DateTimeFormatter;
import java.util.List;

// SymptomAdapter.java
public class SymptomsAdapter extends RecyclerView.Adapter<SymptomsAdapter.SymptomViewHolder> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private List<Episode> symptoms;
    private Context context;

    public SymptomsAdapter(Context context, List<Episode> symptoms) {
        this.context = context;
        this.symptoms = symptoms;
    }

    @NonNull
    @Override
    public SymptomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_symptoms, parent, false);
        return new SymptomViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull SymptomViewHolder holder, int position) {
        Episode symptom = symptoms.get(position);
        holder.symptomName.setText(symptom.getNotes());
        holder.symptomTime.setText(symptom.getTime().format(TIME_FORMATTER));
        // Set icon color based on severity
        int color;
        switch (symptom.getIntensity()) {
            case 1:
                color = ContextCompat.getColor(context, R.color.hot_pink);
                break;
            case 5:
                color = ContextCompat.getColor(context, R.color.light_pink);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.blue);
        }
        holder.symptomIcon.setColorFilter(color);
    }

    @Override
    public int getItemCount() {
        return symptoms.size();
    }

    public void updateSymptoms(List<Episode> newSymptoms) {
        this.symptoms = newSymptoms;
        notifyDataSetChanged();
    }

    static class SymptomViewHolder extends RecyclerView.ViewHolder {
        ImageView symptomIcon;
        TextView symptomName;
        TextView symptomTime;

        SymptomViewHolder(View itemView) {
            super(itemView);
            symptomIcon = itemView.findViewById(R.id.symptomIcon);
            symptomName = itemView.findViewById(R.id.symptomName);
            symptomTime = itemView.findViewById(R.id.symptomTime);
        }
    }
}