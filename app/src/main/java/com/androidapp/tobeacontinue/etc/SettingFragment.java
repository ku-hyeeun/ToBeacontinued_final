package com.androidapp.tobeacontinue.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.androidapp.tobeacontinue.R;



public class SettingFragment extends PreferenceFragment{

    SharedPreferences pref;
    ListPreference language;
    private String languageCode = "en";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        language = (ListPreference)findPreference("language");

    }

    @Override
    public void onResume() {
        super.onResume();

        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();

        pref.unregisterOnSharedPreferenceChangeListener(listener);

    }

    SharedPreferences.OnSharedPreferenceChangeListener listener= new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("sound")){
                boolean b= pref.getBoolean("sound", false);
            }else if(key.equals("vibrate")){
                vibrateMessage();
            }
        }

    };

    public void vibrateMessage(){
        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        if(Build.VERSION.SDK_INT>=26){
            vibrator.vibrate(VibrationEffect.createOneShot(1000,10));
        }else{
            vibrator.vibrate(1000);
        }
    }


}
