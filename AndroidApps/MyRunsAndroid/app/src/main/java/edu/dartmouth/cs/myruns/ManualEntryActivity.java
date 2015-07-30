package edu.dartmouth.cs.myruns;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by garygreene on 2/1/15.
 */
public class ManualEntryActivity extends ListActivity {
    static final String[] MAN_ENTRY = new String[]{"Date",
            "Time", "Duration", "Distance", "Calories", "Heartrate", "Comment"};
    private static final String ACTIVITY_TYPE = "activity_type";
    public SharedPreferences.Editor mEditor;
    public String mKey;
    private Button mSaveButton;
    private Button mCancelButton;
    public ExerciseEntry newEntry;
    public ExerciseEntryDbHelper newHelper;
    public static int[] tempCal;
    public String activity_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entry);
        newEntry = new ExerciseEntry();
        newHelper = new ExerciseEntryDbHelper(this);
        tempCal = new int[6];
        activity_type = getIntent().getStringExtra(ACTIVITY_TYPE);

        mSaveButton = (Button)findViewById(R.id.save);
        mCancelButton = (Button)findViewById(R.id.cancel);



        // Define a new adapter
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, MAN_ENTRY);

        // Assign the adapter to ListView
        setListAdapter(mAdapter);

        // Define the listener interface
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 0) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(),"datePicker");
                }
                if (position == 1) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(),"timePicker");
                }
                if (position == 2) {
                    MyAlertDialogFragment newFragment = new MyAlertDialogFragment().newInstance(R.string.duration_prompt);
                    newFragment.show(getFragmentManager(), "dialog");
                }
                if (position == 3) {
                    MyAlertDialogFragment newFragment = new MyAlertDialogFragment().newInstance(R.string.distance_prompt);
                    newFragment.show(getFragmentManager(), "dialog");
                }
                if (position == 4) {
                    MyAlertDialogFragment newFragment = new MyAlertDialogFragment().newInstance(R.string.calorie_prompt);
                    newFragment.show(getFragmentManager(), "dialog");
                }
                if (position == 5) {
                    MyAlertDialogFragment newFragment = new MyAlertDialogFragment().newInstance(R.string.heartrate_prompt);
                    newFragment.show(getFragmentManager(), "dialog");
                }
                if (position == 6) {
                    MyCommentDialogFragment newFragment = new MyCommentDialogFragment().newInstance(R.string.comment_prompt);
                    newFragment.show(getFragmentManager(), "dialog");
                }
            }
        };

        // Get the ListView and wired the listener
        ListView listView = getListView();
        listView.setOnItemClickListener(mListener);

        // save data and exit program
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityType(activity_type);
                newEntry.setInput(0);
                newEntry.setCalendar(tempCal[0],tempCal[1],tempCal[2],tempCal[3],tempCal[4],tempCal[5]);
                Log.d("cal", "" + newEntry.getDateTimeMillis());
                newHelper.insertEntry(newEntry);

                Log.d("data", newHelper.getReadableDatabase().toString());
                Toast.makeText(getApplicationContext(),
                        getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                newHelper.close();
                finish();
            }
        });

        // exit program without saving
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.profile_cancelled), Toast.LENGTH_SHORT).show();
                newHelper.close();
                finish();
            }
        });

    }

    public void activityType(String actType) {
        switch (actType) {
            case "Running":
                newEntry.setActivity(0);
                break;
            case "Walking":
                newEntry.setActivity(1);
                break;
            case "Standing":
                newEntry.setActivity(2);
                break;
            case "Cycling":
                newEntry.setActivity(3);
                break;
            case "Hiking":
                newEntry.setActivity(4);
                break;
            case "Downhill Skiing":
                newEntry.setActivity(5);
                break;
            case "Cross-Country Skiing":
                newEntry.setActivity(6);
                break;
            case "Snowboarding":
                newEntry.setActivity(7);
                break;
            case "Skating":
                newEntry.setActivity(8);
                break;
            case "Swimming":
                newEntry.setActivity(9);
                break;
            case "Mountain Biking":
                newEntry.setActivity(10);
                break;
            case "Wheelchair":
                newEntry.setActivity(11);
                break;
            case "Elliptical":
                newEntry.setActivity(12);
                break;
            case "Other":
                newEntry.setActivity(13);
                break;
        }
    }
    public void doPositiveClick(int title,EditText edit) {
        // Do stuff here.
        switch (title) {
            case R.string.duration_prompt:
                if (!edit.getText().toString().equals("")) {
                    float minDuration = Float.valueOf(edit.getText().toString());
                    int castDuration = (int) (minDuration * 60);
                    newEntry.setDuration(castDuration);
                }
                break;

            case R.string.distance_prompt:
                if (!edit.getText().toString().equals("")) {
                    float distance = Float.valueOf(edit.getText().toString());
                    int castDistance = (int) distance;
                    newEntry.setDistance(castDistance);
                }
                break;

            case R.string.heartrate_prompt:
                if (!edit.getText().toString().equals("")) {
                    int heartrate = Integer.valueOf(edit.getText().toString());
                    newEntry.setHeartrate(heartrate);
                }
                break;

            case R.string.calorie_prompt:
                if (!edit.getText().toString().equals("")) {
                    int calorie = Integer.valueOf(edit.getText().toString());
                    newEntry.setCalorieManual(calorie);
                }

            case R.string.comment_prompt:
                if (!edit.getText().toString().equals("")) {
                    String comment = edit.getText().toString();
                    newEntry.setComment(comment);
                }
                break;


        }
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manual_entry, menu);
        return true;
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public MyAlertDialogFragment newInstance(int title) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");
            final EditText input = new EditText(getActivity().getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setView(input)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((ManualEntryActivity) getActivity())
                                            .doPositiveClick(title, input);
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((ManualEntryActivity) getActivity())
                                            .doNegativeClick();
                                }
                            }).create();
        }
    }


    public static class MyCommentDialogFragment extends DialogFragment {

        public MyCommentDialogFragment newInstance(int title) {
            MyCommentDialogFragment frag = new MyCommentDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int title = getArguments().getInt("title");
            final EditText input = new EditText(getActivity().getApplicationContext());
            input.setHint("How did it go? Enter notes here.");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setView(input)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((ManualEntryActivity) getActivity())
                                            .doPositiveClick(title, input);
                                }
                            })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    ((ManualEntryActivity) getActivity())
                                            .doNegativeClick();
                                }
                            }).create();
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            tempCal[0] = hourOfDay;
            tempCal[1] = minute;
            tempCal[2] = 0;

        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tempCal[3] = month;
            tempCal[4] = day;
            tempCal[5] = year;

        }
    }
}
