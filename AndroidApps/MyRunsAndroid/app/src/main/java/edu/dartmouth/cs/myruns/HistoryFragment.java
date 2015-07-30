package edu.dartmouth.cs.myruns;


import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class HistoryFragment extends ListFragment {
    private ExerciseEntryDbHelper exerciseEntryDbHelper;
    public int idCode;
    public ArrayAdapter<String> mAdapter;
    public ArrayList<ExerciseEntry> exerciseEntries;
    public ArrayList<String> exerciseStrings;
    public double miToKm = 1.60934;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        exerciseEntryDbHelper = new ExerciseEntryDbHelper(this.getActivity());

        
        displayListView(view);


        return view;
    }

    private void displayListView(View view) {
        exerciseEntries = new ArrayList<ExerciseEntry>();
        exerciseEntries = exerciseEntryDbHelper.fetchEntries();
        Log.d("array", "" + exerciseEntries.size());

        ArrayList<String> exerciseStrings = new ArrayList<String>();
        exerciseStrings = exerciseToString(exerciseEntries);
        ListView listView = (ListView) view.findViewById(android.R.id.list);


        // Define a new adapter
        mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, exerciseStrings);

        // Assign the adapter to ListView
        setListAdapter(mAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> listView, View view,
//                                    int position, long id) {
//                // Get the cursor, positioned to the corresponding row in the result set
//                Log.d("inside","woo");
//                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
//                idCode = cursor.getColumnIndex("_id");
//                ExerciseEntry entry = exerciseEntryDbHelper.fetchEntryByIndex(idCode);
//                Intent intent = new Intent(getActivity(), EntryActivity.class);
//                intent.putExtra(EntryActivity.ENTRY_ID,idCode);
//                startActivity(intent);
//
//            }
//        });
    }

    public void updateMethod() {
        if (mAdapter != null) {
            mAdapter.clear();
            exerciseEntries = exerciseEntryDbHelper.fetchEntries();
            exerciseStrings = exerciseToString(exerciseEntries);
            mAdapter.addAll(exerciseStrings);
            mAdapter.notifyDataSetChanged();
        }
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

    public ArrayList<String> exerciseToString(ArrayList<ExerciseEntry> entries) {
        String mKey = getString(R.string.conversion);
        SharedPreferences mPreference = getActivity().getApplicationContext().getSharedPreferences(mKey, Context.MODE_PRIVATE);
        ArrayList<String> exerciseString  = new ArrayList<String>();
        Iterator<ExerciseEntry> iterator= entries.iterator();
        while (iterator.hasNext()) {
            ExerciseEntry next = iterator.next();
            int inputType = next.getInputType();
            Log.d("input",""+inputType);
            int actType = next.getActivityType();
            String activity = activityType(actType);

            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss MMM dd yyyy");
            long dateTime = next.getDateTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTime);

            int duration = next.getDuration();

            int distance = next.getDistance();

            if (inputType == 0) {
                String entry = activity + ", " + formatter.format(calendar.getTime()) + "\n" + distance + " miles, " + (duration / 60) + " mins " + (duration % 60) + " secs";
                exerciseString.add(entry);
            }
            else {
                double dist = distance * .000621371;
                double finaldist = roundTwoDecimals(dist);
                String entry = activity + ", " + formatter.format(calendar.getTime()) + "\n" + finaldist + " miles, " + (duration / 60) + " mins " + (duration % 60) + " secs";
                exerciseString.add(entry);
            }



        }
        return  exerciseString;
    }
    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // The id argument will be the Run ID
        // Note, that the CursorAdapter gives us this for free
        if (exerciseEntries.get(position).getInputType() == 1) {
            Intent i = new Intent(getActivity(), MapDisplay.class);
            i.putExtra(MapDisplay.ENTRY_ID, id);
            startActivity(i);
        }
        else {
            Intent i = new Intent(getActivity(), EntryActivity.class);
            i.putExtra(EntryActivity.ENTRY_ID, id);
            startActivity(i);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exerciseEntryDbHelper.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMethod();
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }
}
