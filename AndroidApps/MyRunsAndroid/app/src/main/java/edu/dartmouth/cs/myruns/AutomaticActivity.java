package edu.dartmouth.cs.myruns;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
//
//import weka.core.Attribute;
//import weka.core.Instances;

//import weka.core.DenseInstance;



public class AutomaticActivity extends FragmentActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    TextView textx, texty, textz;

    // Map stuff
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //    private ServiceConnection mConnection = this;
//    public TrackingService trackingService;
    public Marker whereAmI;
    private NotificationManager mNotificationManager;
    public Intent serviceIntent;
    boolean firstCall;
    public double lat, lng, alt;
    PolylineOptions rectOptions;
    Polyline polyline;
    int counter, counter2;
    public ExerciseEntry globalEntry;
    LocationManager locationManager;
    double finalclimb, avg_speed;
    public int mTotalDistance, mTotalDuration;
    public String provider;
    public Location startLoc;
    public ExerciseEntryDbHelper mDataHelper;
    public Button mSaveButton,mCancelButton;
    public TextView statsView;
    public String activity_type;
    public double distSoFar,finaldist;
    private static final String INPUT_TYPE = "input_type";
    private static final String ACTIVITY_TYPE = "activity_type";
    private static final int RADIUS = 3959;
    public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;
    public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
    public static final String FEAT_FFT_COEF_LABEL = "fft_coef_";
    public static final String FEAT_MAX_LABEL = "max";
    public static final String FEAT_SET_NAME = "accelerometer_features";
    public static final int FEATURE_SET_CAPACITY = 10000;
    public static final String FEATURE_FILE_NAME = "features.arff";


    public static final String CLASS_LABEL_KEY = "label";
    public static final String CLASS_LABEL_STANDING = "standing";
    public static final String CLASS_LABEL_WALKING = "walking";
    public static final String CLASS_LABEL_RUNNING = "running";
    public static final String CLASS_LABEL_OTHER = "others";
    private static final int mFeatLen = ACCELEROMETER_BLOCK_CAPACITY + 2;

    public static final int SERVICE_TASK_TYPE_COLLECT = 0;
    public static final int SERVICE_TASK_TYPE_CLASSIFY = 1;

    private static ArrayBlockingQueue<Double> mAccBuffer;
    private File mFeatureFile;
    private int mServiceTaskType;
//    private Instances mDataset;
//    private String mLabel;
//    private Attribute mClassAttribute;
    private OnSensorChangedTask mAsyncTask;
    public ArrayList<Double> featVect;
    public FFT fft;
    double[] accBlock;
    double[] re, im;
    double max;
    public float speed;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
//        fft = new FFT(ACCELEROMETER_BLOCK_CAPACITY);
//        accBlock = new double[ACCELEROMETER_BLOCK_CAPACITY];
//        re = accBlock;
//        im = new double[ACCELEROMETER_BLOCK_CAPACITY];
        featVect = new ArrayList<>();
        mAccBuffer = new ArrayBlockingQueue<Double>(ACCELEROMETER_BUFFER_CAPACITY);

       max = Double.MIN_VALUE;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_automatic);
        Log.e("gwrgnkwn","giOncreate");
//        // get textviews
//        textx = (TextView) findViewById(R.id.xval);
//        texty = (TextView) findViewById(R.id.yval);
//        textz = (TextView) findViewById(R.id.zval);
//
//
//        view = findViewById(R.id.textView);
//        view.setBackgroundColor(Color.BLUE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        mSaveButton = (Button)findViewById(R.id.btnSave);
        mCancelButton = (Button)findViewById(R.id.btnCancel);
        statsView = (TextView)findViewById(R.id.type_stats);

        mDataHelper = new ExerciseEntryDbHelper(this);
        globalEntry = new ExerciseEntry();
        counter = 0;
        counter2 = 0;
        avg_speed = 0;

//        String input_type = getIntent().getStringExtra(INPUT_TYPE);
//        inputType(input_type);
        globalEntry.setInput(1);
        // determining activity automatically..figure this part out
        activity_type = "Standing";
//        globalEntry.setActivity(0);
        Time now = new Time();
        now.setToNow();
        globalEntry.setDateTimeMillis(now.toMillis(false));
        distSoFar = 0;
        Log.d("called", "startService");
//        automaticBind();
        rectOptions = new PolylineOptions();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setUpNotification();


        String svcName= Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        //provider = locationManager.getBestProvider(criteria, true);
        provider = locationManager.NETWORK_PROVIDER;
        setUpMapIfNeeded();

        Location l = locationManager.getLastKnownLocation(provider);
        Log.d("loc",""+l.getLatitude());

        LatLng latlng;
        if (l != null) {
            latlng = fromLocationToLatLng(l);
            updateWithNewLocation(l);
        }
        else {
            latlng = new LatLng(0,0);
        }


        whereAmI=mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)));
        // Zoom in
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                17));



        locationManager.requestLocationUpdates(provider, 2000, 10,
                locationListener);

        if (savedInstanceState != null) {
            ArrayList<LatLng> locations = globalEntry.getLocationList();
            Iterator<LatLng> iterator = locations.iterator();
            while (iterator.hasNext()) {
                LatLng nextLoc = iterator.next();
                rectOptions.add(nextLoc);
                polyline = mMap.addPolyline(rectOptions);

            }
        }
        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();
    }
//    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        mFeatureFile = new File(getExternalFilesDir(null), FEATURE_FILE_NAME);
//        Log.d("tag", mFeatureFile.getAbsolutePath());
//
//        // Create the container for attributes
//        ArrayList<Attribute> allAttr = new ArrayList<Attribute>();
//
//        // Adding FFT coefficient attributes
//        DecimalFormat df = new DecimalFormat("0000");
//
//        for (int i = 0; i < ACCELEROMETER_BLOCK_CAPACITY; i++) {
//            allAttr.add(new Attribute(FEAT_FFT_COEF_LABEL + df.format(i)));
//        }
//        // Adding the max feature
//        allAttr.add(new Attribute(FEAT_MAX_LABEL));
//
//        ArrayList<String> labelItems = new ArrayList<String>(3);
//        labelItems.add(CLASS_LABEL_STANDING);
//        labelItems.add(CLASS_LABEL_WALKING);
//        labelItems.add(CLASS_LABEL_RUNNING);
//        labelItems.add(CLASS_LABEL_OTHER);
//        mClassAttribute = new Attribute(CLASS_LABEL_KEY, labelItems);
//        allAttr.add(mClassAttribute);
//
//        // Construct the dataset with the attributes specified as allAttr and
//        // capacity 10000
//        mDataset = new Instances(FEAT_SET_NAME, allAttr, FEATURE_SET_CAPACITY);
//
//        // Set the last column/attribute (standing/walking/running) as the class
//        // index for classification
//        mDataset.setClassIndex(mDataset.numAttributes() - 1);


        //return START_NOT_STICKY;
        return 0;
    }
    @Override
    public void onDestroy() {
        while (!mAsyncTask.isCancelled()) {
            mAsyncTask.cancel(true);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensorManager.unregisterListener(this);
        Log.i("","");
        super.onDestroy();
        mNotificationManager.cancelAll(); // Cancel the persistent notification.


    }
//    @Override
//    protected void onCancelled() {
//
//        Log.e("123", mDataset.size()+"");
//
//        if (mServiceTaskType == SERVICE_TASK_TYPE_CLASSIFY) {
//            super.onCancelled();
//            return;
//        }
//        Log.i("in the loop","still in the loop cancelled");
//        String toastDisp;
//
//        if (mFeatureFile.exists()) {
//
//            // merge existing and delete the old dataset
//            DataSource source;
//            try {
//                // Create a datasource from mFeatureFile where
//                // mFeatureFile = new File(getExternalFilesDir(null),
//                // "features.arff");
//                source = new DataSource(new FileInputStream(mFeatureFile));
//                // Read the dataset set out of this datasource
//                Instances oldDataset = source.getDataSet();
//                oldDataset.setClassIndex(mDataset.numAttributes() - 1);
//                // Sanity checking if the dataset format matches.
//                if (!oldDataset.equalHeaders(mDataset)) {
//                    // Log.d(Globals.TAG,
//                    // oldDataset.equalHeadersMsg(mDataset));
//                    throw new Exception(
//                            "The two datasets have different headers:\n");
//                }
//
//                // Move all items over manually
//                for (int i = 0; i < mDataset.size(); i++) {
//                    oldDataset.add(mDataset.get(i));
//                }
//
//                mDataset = oldDataset;
//                // Delete the existing old file.
//                mFeatureFile.delete();
//                Log.i("delete","delete the file");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            toastDisp = getString(R.string.ui_sensor_service_toast_success_file_updated);
//
//        } else {
//            toastDisp = getString(R.string.ui_sensor_service_toast_success_file_created)   ;
//        }
//        Log.i("save","create saver here");
//        // create new Arff file
//        ArffSaver saver = new ArffSaver();
//        // Set the data source of the file content
//        saver.setInstances(mDataset);
//        Log.e("1234", mDataset.size()+"");
//        try {
//            // Set the destination of the file.
//            // mFeatureFile = new File(getExternalFilesDir(null),
//            // "features.arff");
//            saver.setFile(mFeatureFile);
//            // Write into the file
//            saver.writeBatch();
//            Log.i("batch","write batch here");
//            Toast.makeText(getApplicationContext(), toastDisp,
//                    Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            toastDisp = getString(R.string.ui_sensor_service_toast_error_file_saving_failed);
//            e.printStackTrace();
//        }
//
//        Log.i("toast","toast here");
//        super.onCancelled();
//    }



    @Override
    public void onSensorChanged (SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // call function to add to data queue

            // m = sqrt(x^2+y^2+z^2) and then pass the event/account block (length = 64) into thing that
            // creates the feature vector (empty array of doubles)
            // then pass the feature vector into classifier
            // if data queue length >= 64 -> feature vector
            // classify
            // update UI/variable for activity entry
//            Log.d("here","gotten");
//            Log.d("counter",""+counter2);
//            acceleration(event);
//            if (counter2 >= 64) {
//                Log.d("here","also");
//                max = max(accBlock);
//
//                // Compute the re and im:
//                // setting values of re and im by reference.
//                fft.fft(re, im);
//
//                for (int i = 0; i < re.length; i++) {
//                    Log.d("loop",""+re[i]);
//                    // Compute each coefficient
//                    double mag = Math.sqrt(re[i] * re[i] + im[i]* im[i]);
//                    // Adding the computed FFT coefficient to the
//                    // featVect
//                    featVect.add(Double.valueOf(mag));
//                    // Clear the field
//                    im[i] = .0;
//                }
//                Log.d("got","here");
//                // Finally, append max after frequency components
//                featVect.add(Double.valueOf(max));
//            try {
//                double activity = WekaClassifier.classify(featVect.toArray());
//                if (activity == 0.0) {
//                    activity_type = "Standing";
//                }
//                else if (activity == 1.0) {
//                    activity_type = "Walking";
//                }
//                else if (activity == 2.0) {
//                    activity_type = "Running";
//                }
//                else {
//                    activity_type = "Other";
//                }
//                Log.d("here","too");
//                Toast.makeText(getApplicationContext(),activity_type,Toast.LENGTH_SHORT).show();
//                double finalavg_speed = roundTwoDecimals(avg_speed);
//                int cal = (int)(distSoFar / 15);
//
//                statsView.setText("Type: " + activity_type + "\n Avg speed: " + finalavg_speed +"m/h \n Cur speed: " + speed + "m/h \n Climb: " + finalclimb + "Miles \n Calorie: " + cal + "\n Distance: " + finaldist +"Miles");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            }
            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.

            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));

            }

            double finalavg_speed = roundTwoDecimals(avg_speed);
            int cal = (int) (distSoFar / 15);

            statsView.setText("Type: " + activity_type + "\n Avg speed: " + finalavg_speed + "m/h \n Cur speed: " + speed + "m/h \n Climb: " + finalclimb + "Miles \n Calorie: " + cal + "\n Distance: " + finaldist + "Miles");
//
        }

    }
    private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

//            Instance inst = new DenseInstance(mFeatLen);
//            inst.setDataset(mDataset);
            int blockSize = 0;
            FFT fft = new FFT(ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[ACCELEROMETER_BLOCK_CAPACITY];

            double max = Double.MIN_VALUE;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            Log.d("loop", "" + re[i]);
                            // Compute each coefficient
                            double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                            // Adding the computed FFT coefficient to the
                            // featVect
                            featVect.add(Double.valueOf(mag));
                            // Clear the field
                            im[i] = .0;
                        }
                        Log.d("got", "here");
                        // Finally, append max after frequency components
                        featVect.add(Double.valueOf(max));

                        double activity = WekaClassifier.classify(featVect.toArray());
                        Log.d("activity",""+activity);
                        if (activity == 0.0) {
                            activity_type = "Standing";
                        } else if (activity == 1.0) {
                            activity_type = "Walking";
                        } else if (activity == 2.0) {
                            activity_type = "Running";
                        } else {
                            activity_type = "Other";
                        }
                        Log.d("here", ""+activity_type);
//                        Toast.makeText(getApplicationContext(), activity_type, Toast.LENGTH_SHORT).show();

                        featVect.clear();
                        mAccBuffer.clear();
//                        // Append max after frequency component
//                        inst.setValue(ACCELEROMETER_BLOCK_CAPACITY, max);
//                        inst.setValue(mClassAttribute, mLabel);
//                        mDataset.add(inst);
//                        Log.i("new instance", mDataset.size() + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private double max(double[] accBlock) {
        double max = accBlock[0];
        for (int i = 0; i < accBlock.length; i++) {
            if (accBlock[i] >= max) {
                max = accBlock[i];
            }
        }
        return max;
    }

    private void acceleration(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double acceleration = Math.sqrt(x*x+y*y+z*z);
        if (counter2 != 0 && counter2 % 64 == 0) {
            counter2 = 0;
            accBlock[counter2] = acceleration;
        }
        else {
            accBlock[counter2] = acceleration;
            counter2 = counter2 + 1;
        }


//
//            Object[] vector = new Object[3];
//            vector[0] = x;
//            vector[1] = y;
//            vector[2] = z;




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    private void inputType(String input_type) {
        switch (input_type) {
            case "Manual Entry":
                globalEntry.setInput(0);
                break;
            case "GPS":
                globalEntry.setInput(1);
                break;
            case "Automatic":
                globalEntry.setInput(2);
                break;
        }

    }

    public void onSaveClicked(View v) {
        Location l = locationManager.getLastKnownLocation(provider);
        Time finishTime = new Time();
        finishTime.setToNow();
        long millisFinish = finishTime.toMillis(false);
        long mildiff = millisFinish - globalEntry.getDateTimeMillis();
        mTotalDuration = (int)(mildiff/1000);
        if (activity_type == "Standing") {
            globalEntry.setActivity(2);
        }
        else if (activity_type == "Walking") {
            globalEntry.setActivity(1);
        }
        else if (activity_type == "Running") {
            globalEntry.setActivity(0);
        }
        else {
            globalEntry.setActivity(13);
        }
        globalEntry.setDuration(mTotalDuration);
        globalEntry.setDistance((int)distSoFar);
        float mAvgSpeed = precision(2,(float)avg_speed);
        globalEntry.setAvgSpeed(mAvgSpeed);
        globalEntry.setCalorie();
//        double mClimb = l.getAltitude() - startLoc.getAltitude();
        float mClimb = precision(2,(float)finalclimb);
        globalEntry.setClimb(mClimb);
        mDataHelper.insertEntry(globalEntry);
        Log.d("data", mDataHelper.getReadableDatabase().toString());
        Toast.makeText(getApplicationContext(),
                getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
        mDataHelper.close();
        finish();


    }

    public void onCancelClicked(View v) {
        Toast.makeText(getApplicationContext(),
                getString(R.string.profile_cancelled), Toast.LENGTH_SHORT).show();
        mDataHelper.close();
        finish();
    }
    /**
     * Display a notification in the notification bar.
     */
    private void setUpNotification() {
        Log.d("notification","set up");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AutomaticActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(this.getString(R.string.service_label))
                .setContentText(
                        getResources().getString(R.string.service_started))
                .setSmallIcon(R.drawable.ic_launcher)
                //.setOngoing(true)
                .setContentIntent(contentIntent).build();

        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(0, notification);

    }

    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());

    }


    private void updateWithNewLocation(Location location) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(startLoc.getLatitude(), startLoc.getLongitude())).title("Marker"));

        String latLongString = "No location found";
        String addressString = "No address found";

        counter = counter + 1;
        if (location != null) {
            // Update the map location.

            if (firstCall) {
                lat = startLoc.getLatitude();
                lng = startLoc.getLongitude();
                alt = startLoc.getAltitude();
//                x = RADIUS*Math.cos(lat)*Math.cos(lng);
//                y = RADIUS*Math.cos(lat)*Math.sin(lng);
                firstCall = false;
            }


            LatLng latlng=fromLocationToLatLng(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                    17));
//            double newX = RADIUS*Math.cos(latlng.latitude)*Math.cos(latlng.longitude);
//            double newY = RADIUS*Math.cos(latlng.latitude)*Math.sin(latlng.longitude);
            int mostRecentEntry = globalEntry.getLocationList().size();
            LatLng lastEntry = globalEntry.getLocationList().get(mostRecentEntry-1);
            float[] distanceArray = new float[1];
            Location.distanceBetween(lastEntry.latitude,lastEntry.longitude,latlng.latitude,latlng.longitude,distanceArray);
            distSoFar = distSoFar + distanceArray[0];
            Log.d("dist",""+distanceArray[0]);
//            lat = latlng.latitude;
//            lng = latlng.longitude;
//            x = RADIUS*Math.cos(lat)*Math.cos(lng);
//            y = RADIUS*Math.cos(lat)*Math.sin(lng);
            globalEntry.addToList(latlng);
            rectOptions.add(latlng);
            polyline = mMap.addPolyline(rectOptions);
            Log.d("lat",""+latlng.latitude);
            Log.d("long",""+latlng.longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,
                    17));


            if(whereAmI!=null)
                whereAmI.remove();

            whereAmI=mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN)).title("Current Location"));


            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());

            avg_speed = (avg_speed + location.getSpeed())/counter;
            double climb = (location.getAltitude() - alt)* .000621371;
            finalclimb = roundTwoDecimals(climb);
            double finalavg_speed = roundTwoDecimals(avg_speed);
            int cal = (int)(distSoFar / 15);
            finaldist = roundTwoDecimals(distSoFar * .000621371);
            speed = location.getSpeed();

            // CHANGE ACTIVITY_TYPE!
            statsView.setText("Type: " + activity_type + "\n Avg speed: " + finalavg_speed +"m/h \n Cur speed: " + speed + "m/h \n Climb: " + finalclimb + "Miles \n Calorie: " + cal + "\n Distance: " + finaldist +"Miles");
        }
    }
    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static Float precision(int decimalPlace, Float d) {

        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        startLoc = locationManager.getLastKnownLocation(provider);
        LatLng start = new LatLng(startLoc.getLatitude(),startLoc.getLongitude());
        mMap.addMarker(new MarkerOptions().position(start).title("Start Location"));
        globalEntry.addToList(start);
        Log.d("map","marker added");
        firstCall = true;
    }


    public void drawRoute() {

        ArrayList<LatLng> locations = globalEntry.getLocationList();
        Iterator<LatLng> iterator = locations.iterator();
        rectOptions = new PolylineOptions();
        while (iterator.hasNext()) {
            LatLng nextLoc = iterator.next();


            rectOptions.add(nextLoc);
            rectOptions.color(Color.RED);
            polyline = mMap.addPolyline(rectOptions);
//        PolylineOptions polyLineOptions = new PolylineOptions();
//        polyLineOptions.addAll(locations);
//        mMap.addPolyline(polyLineOptions);
            setUpMapIfNeeded();

        }
}
}

