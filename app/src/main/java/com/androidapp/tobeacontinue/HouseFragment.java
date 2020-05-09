package com.androidapp.tobeacontinue;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.Todolist.OnTabItemSelectedListener;

import java.util.ArrayList;
import java.util.Date;

public class HouseFragment extends Fragment {
    //집에서의 일정 프레그먼트

    private static final String TAG ="HouseFragment";

    RecyclerView recyclerView;      //리사이클러뷰 사용
    NoteAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    @Override
    public void onAttach(Context context) {     //프래그먼트가 액티비티 위에 호출

        super.onAttach(context);
        this.context = context;

        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener)context;
        }
    }

    @Override
    public void onDetach() {                    //프래그먼트가 액티비티에서 내려올 때 호출
        super.onDetach();
        if (context != null){
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_house,container,false);
        initUI(rootView);
        loadNoteListData();

        return rootView;
    }

    public int loadNoteListData() {
        AppConstants.println("loadNoteListData called.");
        String sql = "select _id, CONTENTS, CREATE_DATE, MODIFY_DATE from"+NoteDatabase.TABLE_NOTE+" order by CREATE_DATE desc";

        int recordCount = -1;
        NoteDatabase database = NoteDatabase.getInstance(context);
        if(database != null){
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : "+recordCount+"\n");
            ArrayList<Note> items = new ArrayList<>();

            for(int i=0;i<recordCount;i++){
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                String contents = outCursor.getString(1);
                String date=outCursor.getString(2);
                String createDateStr = null;
                if(date!=null&&date.length()>10){
                    try{
                        Date inDate = AppConstants.dateFormat4.parse(date);
                        createDateStr = AppConstants.dateFormat3.format(inDate);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    createDateStr = "";
                }

                AppConstants.println("# "+i+" -> "+_id+", "+contents+", "+createDateStr);
                items.add(new Note(_id,contents,createDateStr));
            }
            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    private void initUI(ViewGroup rootView){

        Button writeButton = rootView.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onTabSelected(1);
                }
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoteAdapter();
        adapter.addItem(new Note(0,"가스 잠그기","2020-05-06"));
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