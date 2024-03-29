package com.module.config.utils.utils_controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeEventManager implements SensorEventListener {

    private static final int MOV_COUNTS = 5;
    private static final int MOV_THRESHOLD = 4;
    private static final float ALPHA = 0.8F;
    private static final int SHAKE_WINDOW_TIME_INTERVAL = 1000; // milliseconds
    private SensorManager sManager;
    private Sensor s;
    private float gravity[] = new float[3];

    private int counter;
    private long firstMovTime;
    private long lastMoveTime = 0;
    private ShakeListener listener;

    public void setListener(ShakeListener listener) {
        this.listener = listener;
    }

    private static ShakeEventManager instance;

    public static ShakeEventManager getInstance(Context context) {
        if (instance == null) {
            instance = new ShakeEventManager(context);
        }
        return instance;
    }

    public ShakeEventManager(Context context) {
        init(context);
    }

    public void init(Context ctx) {
        sManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        s = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        register();
    }

    public void register() {
        sManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            float maxAcc = calcMaxAcceleration(sensorEvent);
            if (maxAcc >= MOV_THRESHOLD) {
                if (counter == 0) {
                    counter++;
                    firstMovTime = System.currentTimeMillis();
                } else {
                    long now = System.currentTimeMillis();
                    if ((now - firstMovTime) < SHAKE_WINDOW_TIME_INTERVAL)
                        counter++;
                    else {
                        resetAllData();
                        //counter++;
                        return;
                    }

                    if (counter == MOV_COUNTS && (System.currentTimeMillis() - lastMoveTime) > 5000)
                        if (listener != null) {
                            resetAllData();
                            listener.onShake();
                            lastMoveTime = System.currentTimeMillis();
                        }
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void stop() {
        sManager.unregisterListener(this);
        instance = null;
    }


    private float calcMaxAcceleration(SensorEvent event) {
        gravity[0] = calcGravityForce(event.values[0], 0);
        gravity[1] = calcGravityForce(event.values[1], 1);
        gravity[2] = calcGravityForce(event.values[2], 2);

        float accX = event.values[0] - gravity[0];
        float accY = event.values[1] - gravity[1];
        float accZ = event.values[2] - gravity[2];

        float max1 = Math.max(accX, accY);
        return Math.max(max1, accZ);
    }

    private float calcGravityForce(float currentVal, int index) {
        return ALPHA * gravity[index] + (1 - ALPHA) * currentVal;
    }


    private void resetAllData() {
        counter = 0;
        firstMovTime = System.currentTimeMillis();
    }


    public interface ShakeListener {
        void onShake();
    }

}
