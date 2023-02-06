package com.example.doit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RepeatListAdapter extends RecyclerView.Adapter<RepeatListAdapter.ViewHolder> {
    private List<RepeatList> rlists;

    public RepeatListAdapter(List<RepeatList> rlists){
        this.rlists = rlists;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox chkRepeat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chkRepeat = itemView.findViewById(R.id.chkRepeat);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repeat,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RepeatList repeatList = rlists.get(position);
        holder.chkRepeat.setText(repeatList.getContent());

    }

    @Override
    public int getItemCount() {
        return rlists==null?0:rlists.size();
    }
}
