package com.example.a15011022_digipad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Not> notes;
    private RecyclerView rv;
    private MyAdapter adapter;

    private Button addButton;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect to database and get all the notes
        db = new DatabaseHelper(this);
        notes = db.getAllNotes();


        rv = (RecyclerView) findViewById(R.id.noteList);

        //pass the notes and itemClickListener to adapter
        adapter = new MyAdapter(notes, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Not item) {
                Intent intent1 = new Intent(MainActivity.this,NoteDetail.class);
                //put the clicked item in intent to pass it to next activity
                intent1.putExtra("NOT",item);
                startActivity(intent1);
            }
        });

        //connect recycler view and adapter
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //button to add new note
        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NewNote.class);
                startActivity(intent);
            }
        });

    }


}
