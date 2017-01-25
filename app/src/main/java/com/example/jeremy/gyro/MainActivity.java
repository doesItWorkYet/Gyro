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
        z = (TextView) findViewById(R.id.y);
        mSensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
        Thread t = new Thread((Runnable) MainActivity.this);
        t.setDaemon(false);
        t.start();

    }
public void run(){
    while(true){
        changeX(xyz[0]);
        changeY(xyz[1]);
        changeZ(xyz[2]);

    }
}

    public void changeX(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                x.setText("X: " + new Double(what).toString());
            }
        });
    }
    public void changeZ(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                z.setText("Z: " + new Double(what).toString());
            }
        });
    }
    public void changeY(final double what){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                y.setText("Y: " + new Double(what).toString());
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
                long deltaTime = System.currentTimeMillis() - timeStamp;
                timeStamp = System.currentTimeMillis();
                xyz[0] += deltaTime*dTheta[0];
                xyz[1] += deltaTime*dTheta[1];
                xyz[2] += deltaTime*dTheta[2];
            }
            else{
                timeStamp = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
