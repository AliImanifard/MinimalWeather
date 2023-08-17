package com.ali.minimalweather;

import static android.content.Context.MODE_PRIVATE;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String TAG = "SettingsFragment";
    protected MainActivity mainActivity;
    protected Context context;
    private WeatherCardUtils weatherCardUtils;
    ListPreference measurementModePreference;
    ListPreference nightModePreference;
    SwitchPreference soundEffectsPreference;
    SwitchPreference timeFormat24HPreference;
    CheckBoxPreference notificationsPreference;
    Preference email;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        mainActivity = (MainActivity) getActivity();




        measurementModePreference = findPreference("measurement");
        if (measurementModePreference != null)
            measurementModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedMeasurement = (String) newValue;
                // Restart the activity to apply measurement mode
                mainActivity.recreate();
                mainActivity.restartApp();
                return true;
            });


        nightModePreference = findPreference("night");
        if (nightModePreference != null)
            nightModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedNightMode = (String) newValue;

                preferences = mainActivity.getSharedPreferences("myPageIndexPref",MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("currentPageIndex",2);
                editor.apply();

                switch (selectedNightMode) {
                    case "on":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;

                    case "off":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;

                    case "follow_system":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }

                return true;
            });


        soundEffectsPreference = findPreference("sound_effect");
        if (soundEffectsPreference != null) {

            mainActivity.setSoundEffectEnabled(
                    Objects.requireNonNull(soundEffectsPreference.getSharedPreferences())
                    .getBoolean("sound_effect", true));


            soundEffectsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                mainActivity.setSoundEffectEnabled((boolean) newValue);
                preferences = mainActivity.getSharedPreferences("myPageIndexPref",MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("currentPageIndex",2);
                editor.apply();
                return true;
            });
        }



        timeFormat24HPreference = findPreference("time_format");
        if (timeFormat24HPreference != null){
            mainActivity.setTimeFormat24H(Objects.requireNonNull(timeFormat24HPreference.getSharedPreferences())
                    .getBoolean("time_format", true));

            timeFormat24HPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                mainActivity.setTimeFormat24H((Boolean) newValue);
                mainActivity.recreate();
                mainActivity.restartApp();
                return true;
            });
        }




        notificationsPreference = findPreference("notifications");
        if (notificationsPreference != null){
            mainActivity.setNotificationAllowed(Objects.requireNonNull(notificationsPreference.getSharedPreferences())
                    .getBoolean("notifications", true));

            notificationsPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                mainActivity.setNotificationAllowed((Boolean) newValue);
                preferences = mainActivity.getSharedPreferences("myPageIndexPref",MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("currentPageIndex",2);
                editor.apply();
                return true;
            });
        }




        email = findPreference("email");
        if (email != null) {
            email.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));    // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL,getResources().getString(R.string.ali_imanifard_email));
                intent.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.email_subject));

                try {
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(mainActivity, getResources().getString(R.string.application_not_found), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                return false;
            });
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MainActivity)
            this.mainActivity = (MainActivity) context;
    }

}