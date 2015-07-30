package edu.dartmouth.cs.myruns;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

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
    int counter;
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
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        serviceIntent = new Intent(this,TrackingService.class);
        mSaveButton = (Button)findViewById(R.id.btnSave);
        mCancelButton = (Button)findViewById(R.id.btnCancel);
        statsView = (TextView)findViewById(R.id.type_stats);

        mDataHelper = new ExerciseEntryDbHelper(this);
        globalEntry = new ExerciseEntry();
        counter = 0;
        avg_speed = 0;

//        String input_type = getIntent().getStringExtra(INPUT_TYPE);
//        inputType(input_type);
        globalEntry.setInput(1);
        activity_type = getIntent().getStringExtra(ACTIVITY_TYPE);
        activityType(activity_type);
        Time now = new Time();
        now.setToNow();
        globalEntry.setDateTimeMillis(now.toMillis(false));
        distSoFar = 0;
        Log.d("called","startService");
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
//        provider = locationManager.getBestProvider(criteria, true);
        provider = locationManager.NETWORK_PROVIDER;
        setUpMapIfNeeded();
        if (mMap == null) {
            Log.d("null","map");
        }

        Location l = locationManager.getLastKnownLocation(provider);

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

    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

    }

    @Override
    public void onPause() {
        super.onPause();
//        if(!trackingService.isRunning()) {
//            stopService(serviceIntent);
//        }
//        unbindService(mConnection);
    }

    private void automaticBind() {
//        Log.d("running?",""+trackingService.isRunning());
////        if (trackingService.isRunning()) {
//            Log.d("tag", "C:MyService.isRunning: doBindService()");
//            doBindService();
//        }
    }
//    private void doBindService() {
//        Log.d("bind", "C:doBindService()");
//        bindService(serviceIntent, mConnection,
//                Context.BIND_AUTO_CREATE);
//        startService(serviceIntent);
//        mIsBound = true;
//    }

    public ExerciseEntry getExerciseEntryFromService() {
        ExerciseEntry entry = new ExerciseEntry();
//        entry = trackingService.getNewestEntry();
        Log.d("got","entry from service");
        return entry;
    }
    /**
     * Display a notification in the notification bar.
     */
    private void setUpNotification() {
        Log.d("notification","set up");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
    public void activityType(String actType) {
        if (actType != null) {
        switch (actType) {
            case "Running":
                globalEntry.setActivity(0);
                break;
            case "Walking":
                globalEntry.setActivity(1);
                break;
            case "Standing":
                globalEntry.setActivity(2);
                break;
            case "Cycling":
                globalEntry.setActivity(3);
                break;
            case "Hiking":
                globalEntry.setActivity(4);
                break;
            case "Downhill Skiing":
                globalEntry.setActivity(5);
                break;
            case "Cross-Country Skiing":
                globalEntry.setActivity(6);
                break;
            case "Snowboarding":
                globalEntry.setActivity(7);
                break;
            case "Skating":
                globalEntry.setActivity(8);
                break;
            case "Swimming":
                globalEntry.setActivity(9);
                break;
            case "Mountain Biking":
                globalEntry.setActivity(10);
                break;
            case "Wheelchair":
                globalEntry.setActivity(11);
                break;
            case "Elliptical":
                globalEntry.setActivity(12);
                break;
            case "Other":
                globalEntry.setActivity(13);
                break;
        }
        }
    }
//    /**
//     * Send data to the service
//     *
//     * @param intvaluetosend
//     *            The data to send
//     */
//    private void sendMessageToService(int intvaluetosend) {
////        if (mIsBound) {
////            if (mServiceMessenger != null) {
////                try {
////                    Message msg = Message.obtain(null,
////                            MyService.MSG_SET_INT_VALUE, intvaluetosend, 0);
////                    msg.replyTo = mMessenger;
////                    mServiceMessenger.send(msg);
////                } catch (RemoteException e) {
////                }
////            }
////        }
//    }
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

            statsView.setText("Type: " + activity_type + "\n Avg speed: " + finalavg_speed +"m/h \n Cur speed: " + location.getSpeed() + "m/h \n Climb: " + finalclimb + "Miles \n Calorie: " + cal + "\n Distance: " + finaldist +"Miles");
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
     * If it isn't installed {@link SupportMapFragment} (and
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

//    @Override
//    public void onServiceConnected(ComponentName name, IBinder service) {
//
//        Log.d("called","onServiceConnected");
//        trackingService = ((TrackingService.TrackerBinder)service).getService();
//        globalEntry = getExerciseEntryFromService();
//        lat = globalEntry.getLocationList().get(0).latitude;
//        lng = globalEntry.getLocationList().get(0).longitude;
//        Log.d("lat long","assigned");
////        setUpMap();
//        drawRoute();
//
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        trackingService = null;
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancelAll(); // Cancel the persistent notification.

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
//    /**
//     * Handle incoming messages from TimerService
//     */
//    private class IncomingMessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
////            Log.d(TAG, "C:IncomingHandler:handleMessage");
////            switch (msg.what) {
////                case MyService.MSG_SET_INT_VALUE:
////                    Log.d(TAG, "C: RX MSG_SET_INT_VALUE");
////                    textIntValue.setText("Int Message: " + msg.arg1);
////                    break;
////                case MyService.MSG_SET_STRING_VALUE:
////                    String str1 = msg.getData().getString("str1");
////                    Log.d(TAG, "C:RX MSG_SET_STRING_VALUE");
////                    textStrValue.setText("Str Message: " + str1);
////                    break;
////                default:
////                    super.handleMessage(msg);
//            }
//        }
    }

