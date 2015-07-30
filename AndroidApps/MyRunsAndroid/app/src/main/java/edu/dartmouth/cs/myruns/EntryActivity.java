package edu.dartmouth.cs.myruns;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by garygreene on 2/1/15.
 */
public class EntryActivity extends Activity {


    private static final String KEY_ENTRY = "entry_key";
    private static final String NO_CRASHING = "no_crashing";
    private TextView mActivityType;
    private TextView mDateTime;
    private TextView mDuration;
    private TextView mDistance;

    private static final String TAG = "EntryActivity";
    public static final String ENTRY_ID = "ENTRY_ID";

    private ExerciseEntryManager mEntryManager;
    public ExerciseEntryDbHelper mHelper;
    private Location mLastLocation;
    private ExerciseEntry mEntry;

    private static final int menu_delete = Menu.FIRST;
    private static final int NEW_MENU = 0;

    public long entryId;
    public double miToKm = 1.60934;
    public ArrayList<String> entryPieces;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_fragment);
        mEntryManager = ExerciseEntryManager.get(this);
        mHelper = new ExerciseEntryDbHelper(this);
        entryPieces = new ArrayList<String>();

                if (savedInstanceState == null) {
                    // During initial setup, plug in the details fragment.

                    // create fragment and commit a fragment transaction
                    // but before that drive the life cycle for fun
                    Log.d(TAG, "RunActivity: OnCreate() add and commit");

                    entryId = getIntent().getLongExtra(ENTRY_ID, -1);
                    // check for a Run ID as an argument and find the run
                    // basically recover the run if the app
//        Bundle args = savedInstanceState.getBundle(ARG_ENTRY_ID);
//        if (args != null) {
//            long entryId = args.getLong(ARG_ENTRY_ID, -1);
                    if (entryId != -1) {
                        mEntry = mEntryManager.getEntry(entryId + 1);

                        //mLastLocation = mEntryManager.getLastLocationForRun(runId);
                    }
                }
                    mActivityType = (TextView) findViewById(R.id.activity_type);
                    mDateTime = (TextView) findViewById(R.id.date_time);
                    mDuration = (TextView) findViewById(R.id.duration);
                    mDistance = (TextView) findViewById(R.id.distance);

                    if (savedInstanceState != null) {
                        entryPieces = savedInstanceState.getStringArrayList(NO_CRASHING);
                        if (entryPieces.size() >= 4) {
                            mActivityType.setText(entryPieces.get(0));
                            mDateTime.setText(entryPieces.get(1));
                            mDuration.setText(entryPieces.get(2));
                            mDistance.setText(entryPieces.get(3));
                        }
                    }
                    else {
                        updateUI();
                    }
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
                updateUI();
                this.finish();
                return true;
        }
        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper = new ExerciseEntryDbHelper(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHelper.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(NO_CRASHING,entryPieces);
    }


    public String activityType(int actType) {
        String activity = "";
        switch (actType) {
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

    private void updateUI() {
        String mKey = getString(R.string.conversion);
        SharedPreferences mPreference = getSharedPreferences(mKey, MODE_PRIVATE);


        // issue with populating date with real date and not millis
        // issue with duration? do we need to use the duration seconds?
        // ensure distance is in the correct units (and how are we adding units in?)
        if (mEntry != null) {

            int acttype = mEntry.getActivityType();
            mActivityType.setText(activityType(acttype));
            entryPieces.add(activityType(acttype));

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss MMM dd yyyy");
            long dateTime = mEntry.getDateTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTime);
            String dateAndTime = formatter.format(calendar.getTime());
            mDateTime.setText(String.valueOf(dateAndTime));
            entryPieces.add(dateAndTime);

            int duration = mEntry.getDuration();
            String durationString = (duration / 60) + "mins " + (duration % 60) + "secs";
            mDuration.setText(durationString);
            entryPieces.add(durationString);
//            if (mKey.equals("Imperial (Miles)")) {
            String distanceString = mEntry.getDistance() + " miles";
            mDistance.setText(distanceString);
            entryPieces.add(distanceString);
//            }
//            else {
//                double distanceKm = mEntry.getDistance()*miToKm;
//                String distanceString = distanceKm + " kilometers";
//                mDistance.setText(distanceString);
//            }


        }

    }


}
