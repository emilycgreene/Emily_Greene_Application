package edu.dartmouth.cs.myruns;


import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;



/**
 * Created by garygreene on 1/31/15.
 */
public class ExerciseEntry {
    private long id;

    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private Calendar mDateTime;    // When does this entry happen
    private int mDuration;         // Exercise duration in seconds
    private int mDistance;      // Distance traveled. Either in meters or feet.
//    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private int mCalorie;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private ArrayList<LatLng> mLocationList; // Location list

    public ExerciseEntry() {
        id = -1;
        mDateTime = Calendar.getInstance();
        mLocationList = new ArrayList<LatLng>();
        mHeartRate = 0;
    }

    public int getInputType() {
        return mInputType;
    }

    public int getActivityType() {
        return mActivityType;
    }

//    public String getDateTime() {
//        int hour = mDateTime.HOUR_OF_DAY;
//        int min = mDateTime.MINUTE;
//        int sec = mDateTime.SECOND;
//        int month = mDateTime.MONTH;
//        int day = mDateTime.DATE;
//        int year = mDateTime.YEAR;
//        return hour + ":" + min + ":" + sec + " " + month + " " + day + " " + year;
//    }
//
//    public int[] getDate() {
//        int hour = mDateTime.HOUR_OF_DAY;
//        int min = mDateTime.MINUTE;
//        int sec = mDateTime.SECOND;
//        int month = mDateTime.MONTH;
//        int day = mDateTime.DATE;
//        int year = mDateTime.YEAR;
//        int[] date = {hour,min,sec,month,day,year};
//        return date;
//    }

    public void addToList(LatLng loc) {
        mLocationList.add(loc);
    }
    public long getDateTimeMillis() {
        return mDateTime.getTimeInMillis();
    }
    public int getDuration() {
        return mDuration;
    }

    public int getDistance() {
        return mDistance;
    }



//    public double getAvgPace() {
//        return mAvgPace;
//    }
//
//    public double getAvgSpeed() {
//        return mAvgSpeed;
//    }
//
    public int getCalories() {
        return mCalorie;
    }

    public double getClimb() {
        return mClimb;
    }

    public String getComment() {
        return mComment;
    }

//    public int getPrivacy() {
//        return 0; //how do we get privacy selection from xml?
//    }

    public ArrayList<LatLng> getLocationList() {
        return mLocationList;
    }

    public static byte[] toByteArray(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }


    // Convert Location ArrayList to byte array, to store in SQLite database
    public byte[] getLocationByteArray() {
        int[] intArray = new int[mLocationList.size() * 2];

        for (int i = 0; i < mLocationList.size(); i++) {
            intArray[i * 2] = (int) (mLocationList.get(i).latitude * 1E6);
            intArray[(i * 2) + 1] = (int) (mLocationList.get(i).longitude * 1E6);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
                * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }
    // Convert byte array to Location ArrayList
    public void setLocationListFromByteArray(byte[] bytePointArray) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        int locationNum = intArray.length / 2;

        for (int i = 0; i < locationNum; i++) {
            LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
                    (double) intArray[i * 2 + 1] / 1E6F);
            mLocationList.add(latLng);
        }
    }

    public byte[] getBlobLocations() {
        // iterate through the arraylist
        // for each entry, pull out the lat/lng and store it in the next two indices of the bytearray?
        // ensure that the lat is in the even indices
        // bytearray -> blob

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        Iterator<LatLng> iterator = mLocationList.iterator();
        while (iterator.hasNext()) {
            LatLng nextLoc = iterator.next();
            double lat = nextLoc.latitude;
            double lng = nextLoc.longitude;
            try {
                out.writeDouble(lat);
                out.writeDouble(lng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    public long getId() {
        return id;
    }

    public int getDurationSeconds(long endMillis) {
        return (int)((endMillis - mDateTime.getTimeInMillis())/ 1000);
    }

    public static String formatDuration(long durationSeconds) {
        long seconds = durationSeconds % 60;
        long minutes = ((durationSeconds - seconds) / 60) % 60;
        long hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setActivity(int actType) {
        mActivityType = actType;
    }

    public void setId(long entryId) {
        id = entryId;
    }

    public void setDateTimeMillis(long startDateTimeMillis){
        mDateTime.setTimeInMillis(startDateTimeMillis);

    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setDistance(int distance) {
        this.mDistance = distance;
    }

    public void setCalendar(int hour, int min, int sec, int month, int day, int year) {
        mDateTime.set(Calendar.HOUR_OF_DAY,hour);
        mDateTime.set(Calendar.MINUTE,min);
        mDateTime.set(Calendar.SECOND,sec);
        mDateTime.set(Calendar.MONTH,month);
        mDateTime.set(Calendar.DATE,day);
        mDateTime.set(Calendar.YEAR,year);
    }

    public void setInput(int input) {
        mInputType = input;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.mAvgSpeed = (double) avgSpeed;
    }

    public void setHeartrate(int heartrate) {
        this.mHeartRate = heartrate;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public void setLocList(byte[] blob) {
        int i =0;
        while (i<blob.length) {
            if (blob.length % 2 == 0) {
                double lat = blob[i];
                double lng = blob[i + 1];
                LatLng loc = new LatLng(lat,lng);
                mLocationList.add(loc);
            }
            i = i + 2;
        }
    }

    public void setCalorie() {
        double kmDist = mDistance * 1.609344;
        mCalorie = (int) kmDist / 15;
    }

    public void setCalorieManual(int cal) {
        mCalorie = cal;
    }

    public void setClimb(float alt) {
        mClimb = (double) alt;
    }

    public int getHeartrate() {
        return mHeartRate;
    }
}
