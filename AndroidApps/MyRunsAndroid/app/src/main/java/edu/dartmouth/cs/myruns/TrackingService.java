package edu.dartmouth.cs.myruns;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

/**
 * Created by macadmin on 2/7/15.
 */
public class TrackingService extends Service implements LocationListener{
    private LocationManager locationManager;
    private static boolean isRunning = false;
    private Timer mTimer = new Timer();
    private int counter = 0, incrementBy = 1;
    public ExerciseEntry newEntry;
    private LatLng mLocation;
//    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps
//    // track of
//    // all
//    // current
//    // registered
//    // clients.
////    public static final int MSG_REGISTER_CLIENT = 1;
////    public static final int MSG_UNREGISTER_CLIENT = 2;
////    public static final int MSG_SET_INT_VALUE = 3;
////    public static final int MSG_SET_STRING_VALUE = 4;
//////    private final Messenger mMessenger = new Messenger(
////            new IncomingMessageHandler()); // Target we publish for clients to
////    // send messages to IncomingHandler.
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

    public void onCreate() {
        super.onCreate();
        newEntry = initExerciseEntry();
        Log.d("on create happened", "S:onCreate(): Service Started.");
        isRunning = true;

        startLocationUpdates();


        //mTimer.scheduleAtFixedRate(new MyTask(), 0, 5000L);

    }

    private void startLocationUpdates() {

        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);
        Log.d("location","update started:");
        Log.d("lat", ""+location.getLatitude());
        Log.d("long",""+location.getLongitude());
        mLocation = new LatLng(location.getLatitude(),location.getLongitude());
        newEntry.addToList(mLocation);

//        updateWithNewLocation(l);

//        locationManager.requestLocationUpdates(provider, 2000, 10,
//                locationListener);


    }

    private ExerciseEntry initExerciseEntry() {
        Log.d("new","exercise entry");
        newEntry = new ExerciseEntry();
        return newEntry;
    }




    /**
     * Send the data to all registered clients.
     *
     * @param intvaluetosend
     *            The value to send.
     */
    private void sendMessageToUI(int intvaluetosend) {
//        Log.d(TAG, "S:sendMessageToUI");
//        Iterator<Messenger> messengerIterator = mClients.iterator();
//        while (messengerIterator.hasNext()) {
//            Messenger messenger = messengerIterator.next();
//            try {
//                // Send data as an Integer
//                Log.d(TAG, "S:TX MSG_SET_INT_VALUE");
//                messenger.send(Message.obtain(null, MSG_SET_INT_VALUE,
//                        intvaluetosend, 0));
//
//                // Send data as a String
//                Bundle bundle = new Bundle();
//                bundle.putString("str1", "ab" + intvaluetosend + "cd");
//                Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
//                msg.setData(bundle);
//                Log.d(TAG, "S:TX MSG_SET_STRING_VALUE");
//                messenger.send(msg);
//
//            } catch (RemoteException e) {
//                // The client is dead. Remove it from the list.
//                mClients.remove(messenger);
//            }
//        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean running) {
           isRunning = running;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("start", "S:onStartCommand(): Received start id " + startId + ": "
                + intent);
        return START_STICKY; // Run until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        Log.d("destroy", "S:onDestroy():Service Stopped");
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        counter = 0;
        isRunning = false;
    }
    public class TrackerBinder extends Binder {
        TrackingService getService() {
            return TrackingService.this;
        }
    }

    private final IBinder binder = new TrackerBinder();

    // should we be calling this method somewhere?
    // should we be getting the intent from the maps activity and do something with it?
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getLocationsCount() {
        return newEntry.getLocationList().size();
    }

    public ArrayList<LatLng> getLocations() {
        return newEntry.getLocationList();
    }
    public ExerciseEntry getNewestEntry() {
        return newEntry;
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d("TrackingService", "Adding new location");
        mLocation = new LatLng(location.getLatitude(),location.getLongitude());
        newEntry.addToList(mLocation);
        newEntry.setAvgSpeed(location.getSpeed());

    }
    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }


//    /**
//     * Handle incoming messages from MapsActivity
//     */
//    private class IncomingMessageHandler extends Handler { // Handler of
//        // incoming messages
//        // from clients.
//        @Override
//        public void handleMessage(Message msg) {
////            Log.d(TAG, "S:handleMessage: " + msg.what);
////            switch (msg.what) {
////                case MSG_REGISTER_CLIENT:
////                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) ");
////                    mClients.add(msg.replyTo);
////                    break;
////                case MSG_UNREGISTER_CLIENT:
////                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.remove(msg.replyTo) ");
////                    mClients.remove(msg.replyTo);
////                    break;
////                case MSG_SET_INT_VALUE:
////                    incrementBy = msg.arg1;
////                    break;
////                default:
////                    super.handleMessage(msg);
//            }
//        }
    }

