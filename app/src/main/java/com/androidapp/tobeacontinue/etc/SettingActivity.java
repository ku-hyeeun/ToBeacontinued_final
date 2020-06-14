package com.androidapp.tobeacontinue.etc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceActivity;

import com.androidapp.tobeacontinue.R;

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_setting);
        //Settingfragment를 이용할 때는 onCreateView가 아닌 addPreferencesFromResource를 이용해야 함
    }



}
