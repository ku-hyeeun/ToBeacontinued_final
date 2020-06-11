package com.androidapp.tobeacontinue.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.Todolist.CafeteriaTodolist;
import com.androidapp.tobeacontinue.Todolist.ExtraTodolist;
import com.androidapp.tobeacontinue.Todolist.HouseTodolist;
import com.androidapp.tobeacontinue.Todolist.OutsideTodolist;
import com.androidapp.tobeacontinue.Todolist.SchoolTodolist;


public class BeaconFragment extends Fragment {
    //비콘 프레그먼트

    public BeaconFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);    //fragment_home2.xml이 화면에 보여짐

        ImageButton house = view.findViewById(R.id.houseButton);
        ImageButton outside = view.findViewById(R.id.outsideButton);
        ImageButton school = view.findViewById(R.id.schoolButton);
        ImageButton cafeteria = view.findViewById(R.id.cafeteriaButton);        //fragment_home2.xml파일에 있는 5개의 버튼 중 장소 버튼 4개를 객체화

        ImageButton addButton = view.findViewById(R.id.addButton);

        //각 버튼을 클릭하면 해당 activity로 이동
        house.setOnClickListener(new View.OnClickListener() {                       //버튼이 클릭시 Todolist 액티비티로 넘어가게 하였음
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HouseTodolist.class);
                startActivity(intent);
            }
        });

        outside.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OutsideTodolist.class);
                startActivity(intent);
            }
        });

        school.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SchoolTodolist.class);
                startActivity(intent);
            }
        });

        cafeteria.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CafeteriaTodolist.class);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ExtraTodolist.class);
                startActivity(intent);
            }
        });

        return view;

    }
}



