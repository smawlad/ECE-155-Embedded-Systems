package lab0_sss_xx.uwaterloo.ca.cauwaterloolab1_sss_xx;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static lab0_sss_xx.uwaterloo.ca.cauwaterloolab1_sss_xx.Lab1.graph;

public class Lab1 extends AppCompatActivity {
    public static LineGraphView graph;
    float[][] array = new float[100][3];
    String[] timestamp= new String[100];

    private void writeToFile(){
        File file = null; // gets you the reference to the file
        PrintWriter writer = null; //gives you acess to writer to the buffer
        try{
            file = new File(getExternalFilesDir("CSVFile"), "AccData.csv"); //gets me unbuffered writing privilige to the csv file
            writer = new PrintWriter(file); //gives the advantage of buffered writing efficiency

            //writes array of last 100 values of the accelerometer to the file 
            writer.println("X-Axis, Y-Axis, Z-Axis, Time (HH:MM:SS)");
            for (int i=0; i<100;i++){
                writer.println(String.format("%.3f, %.3f, %.3f, "+timestamp[i], array[i][0], array[i][1], array[i][2]));
            }
        } catch(IOException e){
            Log.d("Write Warning", "File Write Fail: " + e.toString()); //catches, if failure to write to file occurs
        } finally{
            writer.flush();
            writer.close();
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);
        LinearLayout r1 = (LinearLayout) findViewById(R.id.activity_lab1);
        r1.setOrientation(LinearLayout.VERTICAL);
        graph = new LineGraphView(getApplicationContext(), 300, Arrays.asList("x", "y", "z"));
        r1.addView(graph);
        graph.setVisibility(View.VISIBLE);

        //2 buttons
        //button 1 for clearing the max value data of all 5 sensors
        //buton 2 for recording graph of the last 100 values for the accelerometer
        Button button1 = new Button (getApplicationContext());
        button1.setText("Clear Record-High Data");
        r1.addView(button1);

        Button button2 = new Button (getApplicationContext());
        button2.setText("Generate CSV Record for Acc.Sen.");
        r1.addView(button2);

        //headings for all 5 sensors
        //outputs readings from the handlers as well as the max values
        TextView Heading = new TextView(getApplicationContext());
        r1.addView(Heading);
        Heading.setTextSize(24);
        Heading.setText("Sensor Data App");

        TextView lightHeading = new TextView(getApplicationContext());
        r1.addView(lightHeading);
        lightHeading.setTextSize(16);
        lightHeading.setText("The Light Sensor Reading is: ");
        TextView lightData = new TextView(getApplicationContext());
        r1.addView(lightData);

        TextView lightMaxHeading = new TextView(getApplicationContext());
        r1.addView(lightMaxHeading);
        lightMaxHeading.setTextSize(16);
        lightMaxHeading.setText("The Record-High Light Sensor Reading is: ");
        TextView lightMax = new TextView(getApplicationContext());
        r1.addView(lightMax);

        TextView space2 = new TextView(getApplicationContext());
        r1.addView(space2);

        TextView accHeading = new TextView(getApplicationContext());
        r1.addView(accHeading);
        accHeading.setTextSize(16);
        accHeading.setText("The Accelerometer Reading is: ");
        TextView accData = new TextView(getApplicationContext());
        r1.addView(accData);

        TextView accMaxHeading = new TextView(getApplicationContext());
        r1.addView(accMaxHeading);
        accMaxHeading.setTextSize(16);
        accMaxHeading.setText("The Record-High Accelerometer Reading is: ");
        TextView accMax = new TextView(getApplicationContext());
        r1.addView(accMax);

        TextView space = new TextView(getApplicationContext());
        r1.addView(space);

        TextView magHeading = new TextView(getApplicationContext());
        r1.addView(magHeading);
        magHeading.setTextSize(16);
        magHeading.setText("The Magnetic Sensor Reading is: ");
        TextView magData = new TextView(getApplicationContext());
        r1.addView(magData);
        TextView magMaxHeading = new TextView(getApplicationContext());
        r1.addView(magMaxHeading);
        magMaxHeading.setTextSize(16);
        magMaxHeading.setText("The Record-High Magnetic Sensor Reading is: ");
        TextView magMax = new TextView(getApplicationContext());
        r1.addView(magMax);

        TextView space1 = new TextView(getApplicationContext());
        r1.addView(space1);

        TextView rotHeading = new TextView(getApplicationContext());
        r1.addView(rotHeading);
        rotHeading.setTextSize(16);
        rotHeading.setText("The Rotation Vector Reading is: ");
        TextView rotData = new TextView(getApplicationContext());
        r1.addView(rotData);

        TextView rotMaxHeading = new TextView(getApplicationContext());
        r1.addView(rotMaxHeading);
        rotMaxHeading.setTextSize(16);
        rotMaxHeading.setText("The Record-High Rotation Vector Reading is: ");
        TextView rotMax = new TextView(getApplicationContext());
        r1.addView(rotMax);

        // SensorManager is a system service that is provided by the Android System. It
  			//tells us whether each sensor is installed, and it is also where the listeners
  			//are registered.
        SensorManager lightManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // All 5 sensors
    		// If we have the SensorManager, check whether the sensor is available.
    		// If an accelerometer is installed, fetch it from the SensorManager and register the SensorEventListener.
    		// The argument SensorManager.SENSOR_DELAY_NORMAL specifies how often the listener should be updated
    		// with the latest state of the accelerometer.
    		// sensorManager.registerListener() method returns a Boolean, indicating whether the registration process
    		// worked or not.
        Sensor lightSensor = lightManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        final LightSensorHandler lightHandler = new LightSensorHandler(lightData, lightMax);
        lightManager.registerListener(lightHandler, lightSensor, lightManager.SENSOR_DELAY_NORMAL);

        SensorManager AccManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accSensor = AccManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final AccSensorHandler AccHandler = new AccSensorHandler(accData, accMax);
        AccManager.registerListener(AccHandler, accSensor, AccManager.SENSOR_DELAY_NORMAL);
        //adds most recent readings to the array of last 100 values
        array = AccHandler.getData();
        timestamp= AccHandler.getTime();

        SensorManager magManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor magSensor = magManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        final MagSensorHandler MagHandler = new MagSensorHandler(magData, magMax);
        magManager.registerListener(MagHandler, magSensor, magManager.SENSOR_DELAY_NORMAL);

        SensorManager rotManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor rotSensor = rotManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        final RotSensorHandler RotHandler = new RotSensorHandler(rotData, rotMax);
        rotManager.registerListener(RotHandler, rotSensor, rotManager.SENSOR_DELAY_NORMAL);

        //button for clearing all max readings
        button1.setOnClickListener(
            new View.OnClickListener(){
                public void onClick(View v){
                    lightHandler.clearMax();
                    AccHandler.clearMax();
                    MagHandler.clearMax();
                    RotHandler.clearMax();
                    graph.purge();
                }
            }
        );

        //button click will write to file for graph of last 100 accelerometer readings
        button2.setOnClickListener(
            new View.OnClickListener(){
                public void onClick(View v){
                    writeToFile();
                }
            }
        );

    }
}

//All 5 handler's for the 5 sensors
//onAccuracyChanged for the handlers is required by the SensorEventListener
//- doesn't serve any true purpose

// onSensorChanged() method reads axis values from the SensorEvent
//that are passed to it and updates the TextView text accordingly.

//clearMax(), clears the maximum value readings of the specific sensor

class LightSensorHandler implements SensorEventListener {

    TextView tvData;
    TextView tvMax;
    float max = Integer.MIN_VALUE;

    //we need a constructor
    public LightSensorHandler(TextView targetTV, TextView targetMax){
        this.tvData = targetTV;
        this.tvMax = targetMax;
    }
    public void onAccuracyChanged(Sensor s, int i){
    }
    public void onSensorChanged(SensorEvent se){
        if(se.sensor.getType() == Sensor.TYPE_LIGHT){

            if (max<se.values[0]){
                max = se.values[0];

            tvMax.setText(String.format("%f",max));
            tvData.setText(String.format("%f",se.values[0]));
        }
    }
    public void clearMax(){
        max = Integer.MIN_VALUE;
    }
}

class AccSensorHandler implements SensorEventListener {
    private TextView tvData;
    private TextView tvMax;
    private float[][] array = new float[100][3];
    private String[] timestamp= new String[100];

    private float xMax = Integer.MIN_VALUE;
    private float yMax = Integer.MIN_VALUE;
    private float zMax = Integer.MIN_VALUE;

    //we need a constructor
    public AccSensorHandler(TextView targetTV1,TextView targetTV2){
        this.tvData = targetTV1;
        this.tvMax = targetTV2;
    }

    public void onAccuracyChanged(Sensor s, int i){
    }

    //adds the most recent value to [0][0], [0][1] and [0][2] to the graph and deletes the last values of the array
    public void insert(){
        for (int i=98;i>=0; i--){
            array[i + 1][0] = array[i][0];
            array[i + 1][1] = array[i][1];
            array[i + 1][2] = array[i][2];
            timestamp[i+1]=timestamp[i];
            //System.arraycopy(array,i,array,i+1,1);
        }
    }

    public void onSensorChanged(SensorEvent se){

        if(se.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            //Log.d("Test", String.format("First 10 %f, %f, %f, %f, %f, %f, %f, %f, %f, %f", array[0][0],array[1][0],array[2][0],array[3][0],array[4][0],array[5][0],array[6][0],array[7][0],array[8][0],array[9][0]));
            //Log.d("Test", String.format("Last 10 %f, %f, %f, %f, %f, %f, %f, %f, %f, %f", array[90][0],array[91][0],array[92][0],array[93][0],array[94][0],array[95][0],array[96][0],array[97][0],array[98][0],array[99][0]));
            insert();
            array[0][0] = se.values[0];
            array[0][1] = se.values[1];
            array[0][2] = se.values[2];
            timestamp[0] = new SimpleDateFormat("HH:mm:ss").format(new Date());

            //reassign values if most recent value is greater than the max value
            String data = String.format("( %.2f, %.2f, %.2f)", se.values[0],se.values[1],se.values[2]);
            if (xMax < se.values[0]) {
                xMax = se.values[0];
            }
            if (yMax < se.values[1]) {
                yMax = se.values[1];
            }
            if (zMax < se.values[2]) {
                zMax = se.values[2];
            }
            String dataMax = String.format("( %.2f, %.2f, %.2f)", xMax,yMax,zMax);
            //String dataMax = String.format("%.2f, %.2f, %.2f,%.2f, %.2f, %.2f, %.2f, %.2f, %.2f, ",array[0][0],array[0][1],array[0][2],array[50][0],array[50][1],array[50][2],array[99][0],array[99][1],array[99][2]);

            tvData.setText(data);
            tvMax.setText(dataMax);
            graph.addPoint(se.values);
        }
    }

    public float[][] getData() {
        return array;
    }
    public String[] getTime() {
        return timestamp;
    }
    public void clearMax(){
        xMax = Integer.MIN_VALUE;
        yMax = Integer.MIN_VALUE;
        zMax = Integer.MIN_VALUE;
    }
}

class MagSensorHandler implements SensorEventListener {
    TextView tvData;
    TextView tvMax;
    float xMax = Integer.MIN_VALUE;
    float yMax = Integer.MIN_VALUE;
    float zMax = Integer.MIN_VALUE;

    //we need a constructor
    public MagSensorHandler(TextView targetTV1,TextView targetTV2){
        this.tvData = targetTV1;
        this.tvMax = targetTV2;
    }
    public void onAccuracyChanged(Sensor s, int i){
    }
    public void onSensorChanged(SensorEvent se){

        if(se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){

            //dataOrganizer run = new dataOrganizer(se.values[0], se.values[1], se.values[2], xMax, yMax, zMax,tvData,tvMax);
            String data = String.format("( %.2f, %.2f, %.2f)", se.values[0],se.values[1],se.values[2]);
            if (xMax < se.values[0]) {
                xMax = se.values[0];
            }
            if (yMax < se.values[1]) {
                yMax = se.values[1];
            }
            if (zMax < se.values[2]) {
                zMax = se.values[2];
            }
            String dataMax = String.format("( %.2f, %.2f, %.2f)", xMax,yMax,zMax);

            tvData.setText(data);
            tvMax.setText(dataMax);
        }
    }
    public void clearMax(){
        xMax = Integer.MIN_VALUE;
        yMax = Integer.MIN_VALUE;
        zMax = Integer.MIN_VALUE;
    }
}

class RotSensorHandler implements SensorEventListener {
    TextView tvData;
    TextView tvMax;
    float xMax = Integer.MIN_VALUE;
    float yMax = Integer.MIN_VALUE;
    float zMax = Integer.MIN_VALUE;

    //we need a constructor
    public RotSensorHandler(TextView targetTV1, TextView targetTV2) {
        this.tvData = targetTV1;
        this.tvMax = targetTV2;
    }

    public void onAccuracyChanged(Sensor s, int i) {
    }

    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            //dataOrganizer run = new dataOrganizer(se.values[0], se.values[1], se.values[2], xMax, yMax, zMax,tvData,tvMax);
            String data = String.format("( %.2f, %.2f, %.2f)", se.values[0],se.values[1],se.values[2]);
            if (xMax < se.values[0]) {
                xMax = se.values[0];
            }
            if (yMax < se.values[1]) {
                yMax = se.values[1];
            }
            if (zMax < se.values[2]) {
                zMax = se.values[2];
            }
            String dataMax = String.format("( %.2f, %.2f, %.2f)", xMax,yMax,zMax);

            tvData.setText(data);
            tvMax.setText(dataMax);
        }
    }
    public void clearMax(){
        xMax = Integer.MIN_VALUE;
        yMax = Integer.MIN_VALUE;
        zMax = Integer.MIN_VALUE;
    }
}
