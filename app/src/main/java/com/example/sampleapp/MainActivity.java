package com.example.sampleapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ZMQSubscriberTask subscriberTask;

    private RadarChart chart;
    private List<String> angleValues;
    private List<Float> amplitudeValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.radar_chart);

        // Set the chart properties
        chart.getDescription().setEnabled(false);
        chart.setRotationEnabled(true);
        chart.setWebLineWidth(1f);

        // Configure the angle labels
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(9f);
        xAxis.setGranularity(30f); // Set the angle label increment to 30 degrees

        // Configure the y-axis
        YAxis yAxis = chart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(10f);

        // Configure the legend
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(5f);
        legend.setTextColor(Color.BLACK);

        // Update the angle values list
        angleValues = new ArrayList<>();
        for (int i = 0; i <= 360; i += 30) {
            angleValues.add(String.valueOf(i));
        }

        // Initialize the amplitude values list
        amplitudeValues = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (subscriberTask == null) {
            String zmqAddress = "tcp://192.168.43.232:5555"; // Replace with your actual ZMQ address

            // Create and execute the ZMQSubscriberTask
            subscriberTask = new ZMQSubscriberTask(this);
            subscriberTask.execute(zmqAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscriberTask != null) {
            subscriberTask.cancel(true);
            subscriberTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscriberTask != null) {
            subscriberTask.cancel(true);
            subscriberTask = null;
        }
    }

    public void updateAmplitudeValues(List<Float> updatedValues) {
        amplitudeValues.addAll(updatedValues);
        updateChart();
    }

    private void updateChart() {
        List<RadarEntry> entries = new ArrayList<>();
        for (int i = 0; i < amplitudeValues.size(); i++) {
            float amplitude = amplitudeValues.get(i);
            entries.add(new RadarEntry(amplitude));
        }

        if (chart.getData() == null) {
            RadarDataSet dataSet = new RadarDataSet(entries, "Amplitude");
            dataSet.setColor(Color.RED);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(Color.RED);
            dataSet.setDrawValues(false);

            RadarData radarData = new RadarData(dataSet);
            chart.setData(radarData);
        } else {
            chart.getData().notifyDataChanged();
        }

        chart.invalidate();
    }
}
