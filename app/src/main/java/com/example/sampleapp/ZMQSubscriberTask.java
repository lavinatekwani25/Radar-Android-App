package com.example.sampleapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.util.ArrayList;
import java.util.List;

public class ZMQSubscriberTask extends AsyncTask<String, Void, List<Float>> {
    private ZMQ.Socket zmqSocket;
    private ZContext zmqContext;
    private MainActivity mainActivity;
    private static final String TAG = "ZMQSubscriberTask";

    public ZMQSubscriberTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected List<Float> doInBackground(String... params) {
        if (params.length < 1) {
            // Log an error or throw an exception indicating that the required parameters are missing
            return null;
        }
        String zmqAddress = params[0];

        // Create a ZMQ context and socket
        zmqContext = new ZContext();
        zmqSocket = zmqContext.createSocket(SocketType.SUB);

        // Subscribe to all topics
        zmqSocket.subscribe("".getBytes());

        try {
            // Connect the socket to the ZMQ address
            zmqSocket.connect(zmqAddress);
        } catch (ZMQException e) {
            e.printStackTrace();
            zmqSocket.close();
            zmqContext.destroy();
            return null;
        }

        List<Float> updatedValues = new ArrayList<>();

        while (!isCancelled()) {
            try {
                // Receive a message from the socket
                byte[] message = zmqSocket.recv();

                // Parse the message as a CSV line
                String line = new String(message);
                Log.d(TAG, "Received line: " + line);
                String[] values = line.split(",");
                if (values.length == 2) {
                    float amplitude = Float.parseFloat(values[0].trim());
                    float angle = Float.parseFloat(values[1].trim());
                    Log.d(TAG, "Amplitude: " + amplitude + ", Angle: " + angle);

                    // Update the amplitude values list
                    updatedValues.add(amplitude);
                }
            } catch (ZMQException e) {
                e.printStackTrace();
                break;
            }
        }

        // Close the socket and destroy the ZMQ context
        zmqSocket.close();
        zmqContext.destroy();

        return updatedValues;
    }

    @Override
    protected void onPostExecute(List<Float> updatedValues) {
        super.onPostExecute(updatedValues);

        if (updatedValues != null && !updatedValues.isEmpty()) {
            mainActivity.updateAmplitudeValues(updatedValues);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        // Cleanup resources if needed
    }
}
