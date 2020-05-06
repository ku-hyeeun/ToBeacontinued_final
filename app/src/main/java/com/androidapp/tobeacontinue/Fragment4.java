package com.androidapp.tobeacontinue;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class Fragment4 extends Fragment {

    RecyclerView recyclerView;      //리사이클러뷰 사용
    NoteAdapter adapter;

    Context context;
    OnTabSelectedListener listener;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

        if(context instanceof OnTabSelectedListener){
            listener = (OnTabSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (context != null){
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_4,container,false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(ViewGroup rootView){

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter();
        adapter.addItem(new Note(0,"학생식당에서 점심먹기","2020-05-06"));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void OnItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);
                Toast.makeText(getContext(),"아이템선택됨: "+item.getContents(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}