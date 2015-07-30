package edu.dartmouth.cs.myruns;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;

/**
 * Created by Emily Greene on 2/9/15.
 */
public class MapDisplay extends FragmentActivity {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public TextView statsView;
    PolylineOptions rectOptions;
    Polyline polyline;
    public static final String ENTRY_ID = "ENTRY_ID";
    private ExerciseEntryManager mEntryManager;
    public ExerciseEntryDbHelper mHelper;
    public long entryId;
    private static final int menu_delete = Menu.FIRST;
    private static final int NEW_MENU = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_map);
        setUpMapIfNeeded();
        statsView = (TextView) findViewById(R.id.show_stats);
        ExerciseEntry entry = new ExerciseEntry();
        ExerciseEntryManager mEntryManager = ExerciseEntryManager.get(this);
        ExerciseEntryDbHelper mHelper = new ExerciseEntryDbHelper(this);
        entryId = getIntent().getLongExtra(ENTRY_ID, -1);
        // check for a Run ID as an argument and find the run
        // basically recover the run if the app
//        Bundle args = savedInstanceState.getBundle(ARG_ENTRY_ID);
//        if (args != null) {
//            long entryId = args.getLong(ARG_ENTRY_ID, -1);
        if (entryId != -1) {
            entry = mEntryManager.getEntry(entryId + 1);

            //mLastLocation = mEntryManager.getLastLocationForRun(runId);
            if (entry != null) {
            int activity_num = entry.getActivityType();
            String activity_type = actType(activity_num);
            double avg_speed = entry.getAvgSpeed();
            double finalavg_speed = roundTwoDecimals(avg_speed);
            double climb = entry.getClimb();
            int cal = entry.getCalories();
            int dist = entry.getDistance();
            double distance = dist * .000621371;
            double finaldist = roundTwoDecimals(distance);
            rectOptions = new PolylineOptions();
            byte[] locations = entry.getLocationByteArray();
            ByteBuffer byteBuffer = ByteBuffer.wrap(locations);
            IntBuffer intBuffer = byteBuffer.asIntBuffer();

            int[] intArray = new int[locations.length / Integer.SIZE];
            intBuffer.get(intArray);

            int locationNum = intArray.length / 2;

            for (int i = 0; i < locationNum; i++) {
                LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
                        (double) intArray[i * 2 + 1] / 1E6F);
                rectOptions.add(latLng);
                polyline = mMap.addPolyline(rectOptions);

//            Log.d("loc length",""+locations.length);
            LatLng start = new LatLng((double) intArray[0 * 2] / 1E6F,
                    (double) intArray[0 * 2 + 1] / 1E6F);
            mMap.addMarker(new MarkerOptions().position(start).title("Start Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start,
                    17));
//            int i =0;
//            while (i<locations.length) {
//                if (locations.length % 2 == 0) {
//                    double lat = locations[i];
//                    Log.d("saved lat",""+ locations[i]);
//                    double lng = locations[i + 1];
//                    Log.d("saved long", "" + locations[i+1]);
//                    LatLng loc = new LatLng(lat,lng);
//
//                }
//                i = i + 2;
//            }
            LatLng end = new LatLng((double) intArray[(locationNum-1) * 2] / 1E6F,
                    (double) intArray[(locationNum-1) * 2 + 1] / 1E6F);
            mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_GREEN)).title("End Location"));
            statsView.setText("Type: " + activity_type + "\n Avg speed: " + finalavg_speed + "m/h \n Cur speed: N/A" + "\n Climb: " + climb + "Miles \n Calorie: " + cal + "\n Distance: " + finaldist + "Miles");

        }
    }}}

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(NEW_MENU,menu_delete,menu_delete,"DELETE");
        menuItem.setShowAsAction(menuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case menu_delete:
                mHelper = new ExerciseEntryDbHelper(this);
                mHelper.removeEntry(entryId + 1);

                this.finish();
                return true;
        }
        return false;

    }


    private String actType(int activity_num) {
        String activity = "";
        switch (activity_num) {
            case 0:
                activity = "Running";
                break;
            case 1:
                activity = "Walking";
                break;
            case 2:
                activity = "Standing";
                break;
            case 3:
                activity = "Cycling";
                break;
            case 4:
                activity = "Hiking";
                break;
            case 5:
                activity = "Downhill Skiing";
                break;
            case 6:
                activity = "Cross-Country Skiing";
                break;
            case 7:
                activity = "Snowboarding";
                break;
            case 8:
                activity = "Skating";
                break;
            case 9:
                activity = "Swimming";
                break;
            case 10:
                activity = "Mountain Biking";
                break;
            case 11:
                activity = "Wheelchair";
                break;
            case 12:
                activity = "Elliptical";
                break;
            case 13:
                activity = "Other";
                break;
        }
        return activity;
    }


    @Override
    protected void onResume() {
        super.onResume();

        setUpMapIfNeeded();

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
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Start Location"));
        Log.d("map", "marker added");
    }
}
