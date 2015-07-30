package edu.dartmouth.cs.myruns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by garygreene on 1/31/15.
 */
public class ExerciseEntryDbHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "exercise_entry.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_EXERCISE_TABLE = "create table if not exists exercise_entry ("
            + "_id integer primary key autoincrement, input_type integer, activity_type integer, date_time long, duration integer, distance integer, avg_speed float, calories integer, climb float, heartrate integer, comment text, gps_data blob)";
    //, avg_pace float, , , privacy integer,

    private static final String TABLE_EXERCISE_ENTRY = "exercise_entry";
    private static final String COLUMN_INPUT_TYPE = "input_type";
    private static final String COLUMN_ACTIVITY_TYPE = "activity_type";
    private static final String COLUMN_DATE_TIME = "date_time";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_DISTANCE = "distance";
//    private static final String COLUMN_AVG_PACE = "avg_pace";
    private static final String COLUMN_AVG_SPEED = "avg_speed";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_CLIMB = "climb";
    private static final String COLUMN_HEARTRATE = "heartrate";
    private static final String COLUMN_COMMENT = "comment";
//    private static final String COLUMN_PRIVACY = "privacy";
    private static final String COLUMN_GPS_DATA = "gps_data";
    private static final String COLUMN_ID = "_id";
    private String[] allColumns = {COLUMN_ID,COLUMN_INPUT_TYPE,COLUMN_ACTIVITY_TYPE, COLUMN_DATE_TIME, COLUMN_DURATION, COLUMN_DISTANCE, COLUMN_AVG_SPEED,COLUMN_CALORIES,COLUMN_CLIMB,COLUMN_HEARTRATE,COLUMN_COMMENT,COLUMN_GPS_DATA};

    // Constructor
    public ExerciseEntryDbHelper(Context context) {
        // DATABASE_NAME is, of course the name of the database, which is defined as a tring constant
        // DATABASE_VERSION is the version of database, which is defined as an integer constant
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

        // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( DATABASE_CREATE_EXERCISE_TABLE);
        Log.d("oncreate","called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Insert a item given each column value

    public long insertEntry(ExerciseEntry entry) {
        // use ContentValues object to represent the mapping of column name to values
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INPUT_TYPE, entry.getInputType());
        cv.put(COLUMN_ACTIVITY_TYPE, entry.getActivityType());
        cv.put(COLUMN_DATE_TIME, entry.getDateTimeMillis());
        cv.put(COLUMN_DURATION, entry.getDuration());
        cv.put(COLUMN_DISTANCE, entry.getDistance());
//        cv.put(COLUMN_AVG_PACE, entry.getAvgPace());
        cv.put(COLUMN_AVG_SPEED, entry.getAvgSpeed());
        cv.put(COLUMN_CALORIES, entry.getCalories());
        cv.put(COLUMN_CLIMB, entry.getClimb());
        cv.put(COLUMN_HEARTRATE, entry.getHeartrate());
        cv.put(COLUMN_COMMENT, entry.getComment());
//        cv.put(COLUMN_PRIVACY, entry.getPrivacy());
        cv.put(COLUMN_GPS_DATA, entry.getLocationByteArray());
//        cv.put(COLUMN_ID,entry.getId());

        // do this for all of them

        database = getWritableDatabase();
        // let's do the insert
        long insertId = database.insert(TABLE_EXERCISE_ENTRY, null, cv);
        database.close();

        // for good measure let's read back what we inserted.
        return insertId;
    }

    // Remove an entry by giving its index
    public void removeEntry(long rowIndex) {
        database = getWritableDatabase();
        database.delete(TABLE_EXERCISE_ENTRY, COLUMN_ID + "=" + rowIndex, null);
        database.close();

    }

    // Query a specific entry by its index.
    public ExerciseEntry fetchEntryByIndex(long rowId) {
        Cursor wrapped = getReadableDatabase().query(TABLE_EXERCISE_ENTRY,
                null, // All columns across in the table -- there is only two
                COLUMN_ID + " = " + rowId,  // Look for a run ID
                null, // group by
                null, // order by
                null, // having
                "1"); // limit 1 row returned
        ExerciseEntry exerciseEntry =  cursorToExerciseEntry(wrapped);
        wrapped.close();
        return exerciseEntry;
    }

    // Query the entire table, return all rows
    public ArrayList<ExerciseEntry> fetchEntries() {
        ArrayList<ExerciseEntry> exerciseEntries = new ArrayList<ExerciseEntry>();
        database = getReadableDatabase();
        if (database != null) {
            Log.d("database","entered");
            Cursor cursor = database.query(ExerciseEntryDbHelper.TABLE_EXERCISE_ENTRY,
                    allColumns, null, null, null, null, null);


            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ExerciseEntry entry = cursorToExerciseEntry(cursor);
                //Log.d(TAG, "get comment = " + cursorToComment(cursor).toString());
                exerciseEntries.add(entry);
                cursor.moveToNext();
            }
            // Make sure to close the cursor
            cursor.close();
            database.close();
        }
            return exerciseEntries;
    }

    public ExerciseCursor queryEntries() {
        // equivalent to  "select * from run order by start_date asc"
        // ascending order by default
        Cursor wrapped = getReadableDatabase().query(TABLE_EXERCISE_ENTRY,
                null, null, null, null, null, COLUMN_DATE_TIME + " asc");
        return new ExerciseCursor(wrapped);
    }

    public ExerciseCursor queryEntry(long id) {
        Cursor wrapped = getReadableDatabase().query(TABLE_EXERCISE_ENTRY,
                null, // All columns across in the table -- there is only two
                COLUMN_ID + " = " + id,  // Look for a run ID
                null, // group by
                null, // order by
                null, // having
                "1"); // limit 1 row returned
        return new ExerciseCursor(wrapped);
    }

    // A convenience class to wrap a cursor that returns rows from the "run" table.
    // The getRun() method. This wrapper makes access the cursor easier. Deals in runs
    public class ExerciseCursor extends CursorWrapper {
        public ExerciseCursor(Cursor c) {
            super(c);
        }
        // Returns a Run object configured for the current row
        // or null if the current row is invalid

        public ExerciseEntry getExerciseEntry() {

            if (isBeforeFirst() || isAfterLast())
                return null;
            ExerciseEntry exerciseEntry = new ExerciseEntry();

            long entryId = getLong(getColumnIndex(COLUMN_ID));
            exerciseEntry.setId(entryId);
            exerciseEntry.setInput(getInt(getColumnIndex(COLUMN_INPUT_TYPE)));
            exerciseEntry.setActivity(getInt(getColumnIndex(COLUMN_ACTIVITY_TYPE)));

            long startDateTime = getLong(getColumnIndex(COLUMN_DATE_TIME));
            Log.d("startDateTime",""+startDateTime);
            exerciseEntry.setDateTimeMillis(startDateTime);

            exerciseEntry.setDuration(getInt(getColumnIndex(COLUMN_DURATION)));
            exerciseEntry.setDistance(getInt(getColumnIndex(COLUMN_DISTANCE)));
            exerciseEntry.setAvgSpeed(getFloat(getColumnIndex(COLUMN_AVG_SPEED)));
            exerciseEntry.setCalorie();
            exerciseEntry.setClimb(getFloat(getColumnIndex(COLUMN_CLIMB)));
            exerciseEntry.setHeartrate(getInt(getColumnIndex(COLUMN_HEARTRATE)));
            exerciseEntry.setComment(getString(getColumnIndex(COLUMN_COMMENT)));
            exerciseEntry.setLocationListFromByteArray(getBlob(getColumnIndex(COLUMN_GPS_DATA)));
            return exerciseEntry;
        }
    }

    private ExerciseEntry cursorToExerciseEntry(Cursor cursor) {
        ExerciseEntry entry = new ExerciseEntry();
        entry.setId(cursor.getLong(0));
        entry.setInput(cursor.getInt(1));
        entry.setActivity(cursor.getInt(2));
        entry.setDateTimeMillis(cursor.getLong(3));
        entry.setDuration(cursor.getInt(4));
        entry.setDistance(cursor.getInt(5));
        entry.setAvgSpeed(cursor.getFloat(6));
        entry.setCalorie();
        entry.setClimb(cursor.getFloat(8));
        entry.setHeartrate(cursor.getInt(9));
        entry.setComment(cursor.getString(10));
        entry.setLocList(cursor.getBlob(11));


        return entry;
    }

}
