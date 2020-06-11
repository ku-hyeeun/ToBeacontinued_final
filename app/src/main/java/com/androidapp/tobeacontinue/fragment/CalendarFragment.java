package com.androidapp.tobeacontinue.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.androidapp.tobeacontinue.Calendar.CalendarTodolist;
import com.androidapp.tobeacontinue.R;


public class CalendarFragment extends Fragment {

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        ImageButton calendarButton = view.findViewById(R.id.calendarButton);

        //버튼 클릭시 CalendarTodolist로 이동
        calendarButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CalendarTodolist.class);
                startActivity(intent);
            }
        });

        return view;
    }
}