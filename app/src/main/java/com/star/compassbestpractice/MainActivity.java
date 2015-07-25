package com.star.compassbestpractice;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;

    private SensorEventListener2 mSensorEventListener2;

    private ImageView mCompassImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        mCompassImageView = (ImageView) findViewById(R.id.compass_image);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor magneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorEventListener2 = new SensorEventListener2() {

            float[] magneticValues = new float[3];
            float[] accelerometerValues = new float[3];

            float lastRotateDegree;

            @Override
            public void onFlushCompleted(Sensor sensor) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    magneticValues = event.values.clone();
                } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    accelerometerValues = event.values.clone();
                }

                float[] r = new float[9];

                float[] values = new float[3];

                SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticValues);
                SensorManager.getOrientation(r, values);

//                Log.d("MainActivity", "values[0] is " + Math.toDegrees(values[0]));

                float rotateDegree = (float) - Math.toDegrees(values[0]);

                if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
                    RotateAnimation rotateAnimation = new RotateAnimation(
                            lastRotateDegree, rotateDegree, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f
                    );

                    rotateAnimation.setFillAfter(true);

                    mCompassImageView.startAnimation(rotateAnimation);

                    lastRotateDegree = rotateDegree;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(mSensorEventListener2,
                magneticSensor, SensorManager.SENSOR_DELAY_GAME);

        mSensorManager.registerListener(mSensorEventListener2,
                accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener2);
        }
    }
}
