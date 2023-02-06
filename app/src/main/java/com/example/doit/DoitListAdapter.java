package com.example.doit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoitListAdapter extends RecyclerView.Adapter<DoitListAdapter.DoitListViewHolder> {
    List<todoList> lists;

    public DoitListAdapter(List<todoList> lists){
        this.lists = lists;
    }

    class DoitListViewHolder extends RecyclerView.ViewHolder{

        private TextView tvDate;
        private TextView tvContent;

        public DoitListViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }
    @NonNull
    @Override
    public DoitListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_item, parent, false);
        return new DoitListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoitListViewHolder holder, int position) {

        todoList list = lists.get(position);
        holder.tvDate.setText(list.getDate());
        holder.tvContent.setText(list.getContent());
    }

    @Override
    public int getItemCount() {
        return lists==null?0:lists.size();
    }
}
