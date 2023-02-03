package com.example.doit;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class todoListAdapter extends RecyclerView.Adapter<todoListAdapter.MyViewHolder> {

    private List<todoList> todoLists;
    private List<String> uidKey;
    private todoListListener listener;
    int selectedPosition = -1;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public todoListAdapter(List<todoList> listDto, List<String> uidKey) {
        todoLists = listDto;
        this.uidKey = uidKey;  // todoList에 들어가 있는 uid값을 가져오기 위해 사용
    }

    // Main에 값을 넘겨주기 위해 리스너를 사용
    public void setListener(todoListListener listener){
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private CheckBox chkContent;
        private Button btnDelete;
        private Button btnUpdte;

        public MyViewHolder(@NonNull View itemView, final todoListListener listener) {
            super(itemView);
            chkContent = itemView.findViewById(R.id.chkContent);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdte = itemView.findViewById(R.id.btnUpdate);

            // 수정버튼을 클릭했을 때
            btnUpdte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String key = uidKey.get(position);
                    listener.onItemClick(position, key);
                    chkContent.setVisibility(View.GONE);
                    btnUpdte.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                }
            });
        }
    }

    // position에 대한 값(todolist)를 반환해줌.
    public todoList getItem(int position){
        todoList todolist = todoLists.get(position);
        return todolist;
    }

    @NonNull
    @Override
    public todoListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view, listener);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        todoList todoList = todoLists.get(position);
        holder.chkContent.setText(todoList.getContent());

        // 체크박스 이벤트(chkId가 1이면 계속 클릭된 상태로 만들어줌)
        holder.chkContent.setChecked(todoList.getChkId()==1);
        holder.chkContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // selectedPosition = holder.getAdapterPosition();
                    database.getReference().child("todoList").child(uidKey.get(position)).child("chkId").setValue(1);
                } else {
                    //selectedPosition = -1;
                    database.getReference().child("todoList").child(uidKey.get(position)).child("chkId").setValue(0);
                }


            }
        });

        // 할일 삭제 버튼을 클릭하면 -> 삭제 되게 만듦
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("todoList").child(uidKey.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("error: "+e.getMessage());
                        Toast.makeText(v.getContext(), "삭제실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return todoLists==null?0:todoLists.size();
    }
}
