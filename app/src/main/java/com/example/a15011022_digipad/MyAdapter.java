package com.example.a15011022_digipad;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

    private List<Not> noteList;
    private OnItemClickListener listener;

    public MyAdapter(List<Not> noteList, OnItemClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView headlineView,dateView,smallContentView;
        public MyViewHolder(View v){
            super(v);
            headlineView = (TextView) v.findViewById(R.id.headlineView) ;
            dateView = (TextView) v.findViewById(R.id.dateView);
            smallContentView = (TextView) v.findViewById(R.id.smallContent);
        }

        public void bind(final Not not, final OnItemClickListener listener){

            //show the title, date and the first 30 letter of content
            headlineView.setText(not.getBaslik());
            dateView.setText(not.getTarih().split("\\s+")[0]);
            if( not.getIcerik().length() > 30 )
                smallContentView.setText(not.getIcerik().substring(0,29));
            else
                smallContentView.setText(not.getIcerik());


            //headlineView.setBackgroundColor(Integer.parseInt(not.getRenk()));
            //smallContentView.setBackgroundColor(Integer.parseInt(not.getRenk()));

            //set the background color to selected color
            itemView.setBackgroundColor(Integer.parseInt(not.getRenk()));

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    listener.onItemClick(not);
                }
            });
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        //inflate the view with the basic elements
        View v = inflater.inflate(R.layout.note_raw, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }



    //bind the view and data
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.bind(noteList.get(i),listener);
    }

    //get note count
    public int getItemCount() {

        return noteList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(Not item);
    }

}

