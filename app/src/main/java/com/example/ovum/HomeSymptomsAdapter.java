package com.example.ovum;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ovum.models.Symptom;

import java.util.List;

public class HomeSymptomsAdapter extends RecyclerView.Adapter<HomeSymptomsAdapter.HomeSymptomsViewHolder> {
    private List<Symptom> symptomsList;
    private HomeSymptomsAdapter.OnIconClickListener listener;

    public HomeSymptomsAdapter(List<Symptom> symptomsList, OnIconClickListener listener) {
        this.symptomsList = symptomsList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public HomeSymptomsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_symptoms, parent, false);
        return new HomeSymptomsViewHolder(view);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onBindViewHolder(@NonNull HomeSymptomsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Symptom symptom = symptomsList.get(position);
        holder.symptomDescription.setText(symptom.getSymptomDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.dayOfMonth.setText(symptom.getDate().getDayOfMonth());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.date.setText(symptom.getDate().toString());
        }
        holder.dayOfWeek.setText(symptom.getDate().getDayOfWeek().toString());
        holder.eyeIcon.setOnClickListener(v -> {
            listener.onIconClick(position);
        });


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class HomeSymptomsViewHolder extends RecyclerView.ViewHolder {
        // defining the views of the symptoms items
        TextView dayOfWeek;
        TextView date;
        TextView dayOfMonth;
        TextView symptomDescription;
        ImageView eyeIcon;
        public HomeSymptomsViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeek = itemView.findViewById(R.id.home_day_of_week);
            dayOfMonth = itemView.findViewById(R.id.home_day_of_month);
            date = itemView.findViewById(R.id.symptom_logged_date);
            symptomDescription = itemView.findViewById(R.id.symptom_description);
            eyeIcon = itemView.findViewById(R.id.hide_view);

        }
    }

    public interface OnIconClickListener {
        void onIconClick(int position);
    }

}
