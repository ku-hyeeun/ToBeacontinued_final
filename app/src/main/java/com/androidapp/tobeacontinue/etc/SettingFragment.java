package com.androidapp.tobeacontinue.etc;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.androidapp.tobeacontinue.R;



public class SettingFragment extends PreferenceFragment{

    SharedPreferences pref;                 //SharedPreference 정의

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        //Settingfragment를 이용할 때는 onCreateView가 아닌 addPreferencesFromResource를 이용해야 함

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Sharedpreference를 이용하여 값을 저장함
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
                Toast.makeText(getActivity(), "소리알림 : "+ b, Toast.LENGTH_SHORT).show();
            }else if(key.equals("vibrate")){

            }
        }

    };


}
