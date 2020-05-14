package pt.ulisboa.tecnico.cmov.foodist.view.fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;

import pt.ulisboa.tecnico.cmov.foodist.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Log.i("SettingsFragment", "onCreatePreferences");
        addPreferencesFromResource(R.xml.settings);
    }
}
