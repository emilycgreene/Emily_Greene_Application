package edu.dartmouth.cs.ontime;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by garygreene on 2/17/15.
 */
public class Settings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

//        ListPreference listPreference = (ListPreference) findPreference("list_preference");
//        if (listPreference.getValue() == null) {
//            listPreference.setValueIndex(1);
//        }
//
//        String currValue = listPreference.getValue();
//
//
//        String mKey = getString(R.string.conversion);
//        SharedPreferences mPreference = getActivity().getApplicationContext().getSharedPreferences(mKey, Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor mEditor = mPreference.edit();
//        mEditor.putString(mKey,currValue);
//        mEditor.commit();

            addPreferencesFromResource(R.xml.settings);






        }
    }

