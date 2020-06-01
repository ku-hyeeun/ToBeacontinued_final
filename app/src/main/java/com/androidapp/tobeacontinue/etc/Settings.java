package com.androidapp.tobeacontinue.etc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.androidapp.tobeacontinue.R;

public class Settings extends PreferenceActivity {
    //설정 메뉴

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        SharedPreferences pref;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
                if(key.equals("message")){
                    boolean b= pref.getBoolean("message", false);

                }else if(key.equals("vibrate")){

                }
            }
        };
    }
}
