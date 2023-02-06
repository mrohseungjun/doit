package com.example.doit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoitList extends AppCompatActivity {
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseUser user;
    private RecyclerView recyclerView;
    List<todoList> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        DoitListAdapter adapter = new DoitListAdapter(lists);

//        database.getReference().child("todoList").orderByKey().addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                //변화된 값이 DataSnapshot 으로 넘어온다.
////                //데이터가 쌓이기 때문에  clear()
//                lists.clear();
//
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    todoList todolistDto = ds.getValue(todoList.class);
//                    String uidKey = ds.getKey();
//
//                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(todolistDto.getUid())) {
//                        lists.add(todolistDto);
//                        //uidList.add(uidKey);
//                    }
//                }
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        database.getReference().child("todoList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //변화된 값이 DataSnapshot 으로 넘어온다.
                //데이터가 쌓이기 때문에  clear()
                lists.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    todoList todolistDto = ds.getValue(todoList.class);
                    //String uidKey = ds.getKey();

                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(todolistDto.getUid())) {
                        lists.add(todolistDto);
                        //uidList.add(uidKey);
                    }
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
