package edu.dartmouth.cs.myruns;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StartFragment extends Fragment {
    private static final String INPUT_TYPE = "input_type";
    private static final String ACTIVITY_TYPE = "activity_type";
    private Button mStartButton,mSyncButton;
    public String spinnerValue;
    public String spinnerActivity;
    public Spinner inputSpinner;
    public Spinner activitySpinner;
    public ExerciseEntryDbHelper mHelper;
    ArrayList<ExerciseEntry> entries;
    String everything;
//    public SharedPreferences.Editor mEditor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        mHelper = new ExerciseEntryDbHelper(this.getActivity());
//        String mKey = getString(R.string.preference);
//        final SharedPreferences mPreference = getSharedPreferences(mKey,MODE_PRIVATE);
//
//        mEditor = mPreference.edit();


        inputSpinner = (Spinner) view.findViewById(R.id.inputSpinner);
        activitySpinner = (Spinner) view.findViewById(R.id.activitySpinner);
        mStartButton = (Button)view.findViewById(R.id.start);
        mSyncButton = (Button)view.findViewById(R.id.sync);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerValue = inputSpinner.getSelectedItem().toString();
                spinnerActivity = activitySpinner.getSelectedItem().toString();
                Log.d("log", spinnerActivity);
                if (spinnerValue.equals("Manual Entry")) {
                    Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                    intent.putExtra(INPUT_TYPE,spinnerValue);
                    intent.putExtra(ACTIVITY_TYPE,spinnerActivity);
                    // send in the intent what the value of the spinner as far as activity type is!
                    startActivity(intent);
                }
                else if (spinnerValue.equals("GPS")){
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra(INPUT_TYPE,spinnerValue);
                    intent.putExtra(ACTIVITY_TYPE,spinnerActivity);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), AutomaticActivity.class);
                    intent.putExtra(INPUT_TYPE,spinnerValue);
                    startActivity(intent);

                }

            }
        });

        mSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                postMsg("it worked!");
                JSONArray allEntries = new JSONArray();
                entries = mHelper.fetchEntries();
                Iterator<ExerciseEntry> iterator= entries.iterator();
                while (iterator.hasNext()) {
                    ExerciseEntry next = iterator.next();
                    long id = next.getId();
                    int inputType = next.getInputType();
                    int actType = next.getActivityType();
                    long dateTime = next.getDateTimeMillis();
                    int duration = next.getDuration();
                    int distance = next.getDistance();
                    double avgSpeed = next.getAvgSpeed();
                    int cals = next.getCalories();
                    double climb = next.getClimb();
                    int heartrate = next.getHeartrate();
                    String comment = "N/A";

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("id",id);
                        obj.put("input",inputType);
                        obj.put("activity",actType);
                        obj.put("datetime",dateTime);
                        obj.put("duration",duration);
                        obj.put("distance",distance);
                        obj.put("avgspeed",avgSpeed);
                        obj.put("calories",cals);
                        obj.put("climb",climb);
                        obj.put("heartrate",heartrate);
                        obj.put("comment",comment);
                        allEntries.put(obj);
                  } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }

                everything = allEntries.toString();

                postMsg();
            }
        });
        // TODO: sync button
        // make the button, have it query the database, create cursor, and send everything over using json/http post
        return view;
    }


    private void postMsg() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                String url = getString(R.string.server_addr) + "/post.do";
                String res = "";
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_input", everything);
//                params.put("from", "phone");
                try {
                    res = ServerUtilities.post(url, params);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return res;
            }

            @Override
            protected void onPostExecute(String res) {
//                mPostText.setText("");
//                refreshPostHistory();
            }

        }.execute();
    }

}