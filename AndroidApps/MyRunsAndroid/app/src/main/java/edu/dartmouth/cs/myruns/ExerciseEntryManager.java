package edu.dartmouth.cs.myruns;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import edu.dartmouth.cs.myruns.ExerciseEntryDbHelper.ExerciseCursor;

/**
 * Created by garygreene on 2/1/15.
 */
public class ExerciseEntryManager {
    private static final String TAG = "ExerciseEntryManager";
    public static final String ACTION_LOCATION = "edu.dartmouth.cs.myruns.ACTION_LOCATION";
    private static final String PREFS_FILE = "entries";
    private static final String PREFS_CURRENT_ENTRY_ID = "ExerciseEntryManager.currentRunID";

    private static ExerciseEntryManager sExerciseEntryManager;
    private long mCurrentId;
    private SharedPreferences mPref;
    private ExerciseEntryDbHelper mHelper;
    private Context mAppContext;
    private LocationManager mLocationManger;

    // private constructor that forces the user to use RunManager.get(Context)

    private ExerciseEntryManager(Context appContext) {

        mAppContext = appContext;

        // set up the location manager
        mLocationManger = (LocationManager) mAppContext
                .getSystemService(Context.LOCATION_SERVICE);

        // set up the database to store runs
        mHelper = new ExerciseEntryDbHelper(mAppContext);

        // get the current ID so you can keep incrementing it the app was killed
        mPref = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentId = mPref.getLong(PREFS_CURRENT_ENTRY_ID, -1);
    }

    public static ExerciseEntryManager get(Context c) {
        if (sExerciseEntryManager == null) {
            // user the application context to avoid leaking activities
            sExerciseEntryManager = new ExerciseEntryManager(c.getApplicationContext());
        }
        return sExerciseEntryManager;
    }

    // set up the intents that the location manager fires
    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {

        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);

    }

    // start the location updates.
    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        // get the last known location abd broadcast it if you have one

        Location lastKnown = mLocationManger.getLastKnownLocation(provider);
        if (lastKnown != null) {
            // Reset the time to now
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        // Start updates from the location manager using the GPS as fast as possible
        PendingIntent pi = getLocationPendingIntent(true);
        // min time and min distance set to 0 -- not always a good idea.
        mLocationManger.requestLocationUpdates(provider, 0, 0, pi);

    }

    // get the last known location back to the UI via the RunFragment
    // by broadcasting an intent just like the Location Manger would
    private void broadcastLocation(Location lastKnown) {

        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, lastKnown);
        mAppContext.sendBroadcast(broadcast);
    }


    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManger.removeUpdates(pi);
            pi.cancel();
        }


    }

    // Is the RunManager currently tracking a run
    // we overload the method for two calls in RunFragment
    public boolean isTrackingExercise(ExerciseEntry entry) {
        return entry != null && entry.getId() == mCurrentId;
    }

    public boolean isTrackingExercise() {
        return getLocationPendingIntent(false) != null;
    }

    // **INSERT methods: location and insert run methods. Helper hides
    // a lot of the database specifics

    // create and insert into the database and save the id in the run
    public ExerciseEntry insertEntry() {
        ExerciseEntry entry = new ExerciseEntry();
        entry.setId(mHelper.insertEntry(entry));
        return entry;
    }

//    // insert location into the database
//    public void insertLocation(Location loc) {
//        if (mCurrentId != -1) {
//            mHelper.insertLocation(mCurrentId, loc);
//        } else {
//            Log.e(TAG, "Location received with no tracking run; ignoring");
//        }
//    }
//
//
//    // get a single run and return it from the database
//    public Location getLastLocationForRun(long runId) {
//        Location location = null;
//        LocationCursor cursor = mHelper.queryLastLocation(runId);
//        cursor.moveToFirst();
//        // if you got a row, then get a location
//        if (!cursor.isAfterLast())
//            location = cursor.getLocation();
//        cursor.close();
//        return location;
//    }

    // A number of methods for creating, tracking and
    // stopping runs

    // create a new Run, insert it in the db and start tracking it.
    public ExerciseEntry startNewEntry() {

        // created a new Run and insert it into the db
        ExerciseEntry entry = insertEntry();
        // get the run you just inserted to check
        ExerciseEntry query = getEntry(entry.getId());
        // start tracking the run
        startTrackingExercise(entry);
        return entry;
    }

    // query all the runs in the DB. Returns a RunCursor (it's a wrapper)
    // that includes all the runs in the DB
    public ExerciseCursor queryEntries() {
        return mHelper.queryEntries();
    }

    // get a single run and return it from the database
    public ExerciseEntry getEntry(long id) {
        ExerciseEntry entry = null;
        ExerciseCursor cursor = mHelper.queryEntry(id);
        cursor.moveToFirst();
        // make sure you have a single row
        if (!cursor.isAfterLast())
            entry = cursor.getExerciseEntry();
        cursor.close();
        return entry;
    }

    // start tracking for a run, make sure you save the current ID
    public void startTrackingExercise(ExerciseEntry entry) {

        // Keep the ID
        mCurrentId = entry.getId();
        // save the current run id in case app is killed
        mPref = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mPref.edit().putLong(PREFS_CURRENT_ENTRY_ID, mCurrentId).commit();
        // now start the location updates
        startLocationUpdates();

    }

    // finally, stop the run, make sure we remove the current run ID; it's
    // stop so we don't want the app to restart it if it comes back from being killed;
    // so clear out the id.
    public void stopExercise() {
        stopLocationUpdates();
        mCurrentId = -1;
        mPref.edit().remove(PREFS_CURRENT_ENTRY_ID).commit();
    }
}
