package com.example.ovum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CyclesFragment extends Fragment {


    LineChart mpLineChart;

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cycles, container, false);
        mpLineChart = view.findViewById(R.id.chart);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(),"Cylces For the Previous months");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();

        recyclerView = view.findViewById(R.id.cardRecyleView);
        // intialise the Cycles adapter
        CyclesAdapter cyclesAdapter = new CyclesAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cyclesAdapter);
        return view;
    }

    // method that returns an arraryList of the datavalues for the line chart
    private ArrayList<Entry> dataValues1(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(0,20));
        dataVals.add(new Entry(5,24));
        dataVals.add(new Entry(10,22));
        dataVals.add(new Entry(15,21));
        dataVals.add(new Entry(20,34));
        dataVals.add(new Entry(25,40));
        dataVals.add(new Entry(30,33));
        dataVals.add(new Entry(35,27));
        return dataVals;
    }
}