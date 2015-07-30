package edu.dartmouth.cs.myruns;


import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {



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

        addPreferencesFromResource(R.xml.fragment_settings);






    }
}