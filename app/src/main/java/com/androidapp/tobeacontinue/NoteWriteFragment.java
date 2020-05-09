package com.androidapp.tobeacontinue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.androidapp.tobeacontinue.Todolist.OnTabItemSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteWriteFragment extends Fragment {
    EditText edtText;
    Button savebutton;
    NoteAdapter adapter;
    OnTabItemSelectedListener listener;

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;

        if(context instanceof OnTabItemSelectedListener){
            listener  = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(context !=null){
            context = null;
            listener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_note_write, container, false);
        initUI(rootview);

        return rootview;
    }

    private void initUI(ViewGroup rootview) {
        edtText = rootview.findViewById(R.id.edtMemo);

        savebutton = rootview.findViewById(R.id.btnDone);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = edtText.getText().toString();

                if (str.length() > 0) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String substr = sdf.format(date);
                    Intent intent = new Intent();
                    intent.putExtra("main", str);
                    intent.putExtra("sub", substr);

                    if(listener!=null){
                        listener.onTabSelected(0);
                    }
                }
            }
        });

        rootview.findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(listener !=null){
                    listener.onTabSelected(0);
                }
            }
        });


    }
}