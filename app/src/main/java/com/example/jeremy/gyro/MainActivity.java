package com.example.jeremy.gyro;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements SensorEventListener, Runnable{
    TextView x,y,z;
    private SensorManager mSensorManager;
    long timeStamp = 0;
    double[] xyz = {0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = (TextView) findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        //mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        Thread t = new Thread((Runnable) MainActivity.this);
        t.setDaemon(false);
        t.start();

    }
public void run(){
    while(true){
        changeX(xyz[0]);
        changeY(xyz[1]);
        changeZ(xyz[2]);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
    //when this Activity starts
    @Override
    protected void onResume()
    {
        super.onResume();
        /*register the sensor listener to listen to the gyroscope sensor, use the
        callbacks defined in this class, and gather the sensor information as quick
        as possible*/
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_FASTEST);
    }

    //When this Activity isn't visible anymore
    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        mSensorManager.unregisterListener(this);
        super.onStop();
    }
    public void changeX(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                x.setText("X: " + (new Double((what)).toString()));
            }
        });
    }
    public void changeZ(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                z.setText("Z: " + new Double((what)).toString());
            }
        });
    }
    public void changeY(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                y.setText("Y: " + new Double((what)).toString());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            double[] dTheta = {event.values[0],event.values[1], event.values[2]};
            if(timeStamp != 0){
                double deltaTime = (System.currentTimeMillis() - timeStamp)/1000.0;
                timeStamp = System.currentTimeMillis();
                xyz[0] += Math.toDegrees(deltaTime*dTheta[0]);
                xyz[1] += Math.toDegrees(deltaTime*dTheta[1]);
                xyz[2] += Math.toDegrees(deltaTime*dTheta[2]);
                if(xyz[0] < 0) xyz[0] += 360.0;
                if(xyz[0] > 360) xyz[0] -= 360.0;
                if(xyz[1] < 0) xyz[1] += 360.0;
                if(xyz[1] > 360) xyz[1] -= 360.0;
                if(xyz[2] < 0) xyz[2] += 360.0;
                if(xyz[2] > 360) xyz[2] -= 360.0;
            }
            else{
                timeStamp = System.currentTimeMillis();
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            float[] matrix = new float[9];
            float[] orientation = new float[9];
            mSensorManager.getRotationMatrixFromVector(matrix, event.values);
            //calculate Euler angles now
            SensorManager.getOrientation(matrix, orientation);

            //The results are in radians, need to convert it to degrees
            convertToDegrees(orientation);
            xyz[0] = (orientation[0]);
            xyz[1] = (orientation[1]);
            xyz[2] = (orientation[2]);
        }
    }
    private void convertToDegrees(float[] vector){
        for (int i = 0; i < vector.length; i++){
            vector[i] = Math.round(Math.toDegrees(vector[i]));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
