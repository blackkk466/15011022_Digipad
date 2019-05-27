package com.example.a15011022_digipad;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText headlineDetail, contentDetail;
    private TextView priorityDetail, dateViewDetail;
    private Button updateButton, deleteButton, colorButton;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> priorityAdapter;

    private Not not;
    private int priority;
    private String color;
    private String dateAndTime;
    private String address;
    private long id;

    private DatabaseHelper db;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);

        db = new DatabaseHelper(this);

        //get the intents that come from both notification and mainactivity
        onNewIntent(getIntent());

        //noinspection ConstantConditions
        color = not.getRenk();

        headlineDetail = (EditText) findViewById(R.id.detailHeadline);
        contentDetail = (EditText) findViewById(R.id.detailContent);

        headlineDetail.setText(not.getBaslik());
        contentDetail.setText(not.getIcerik());

        headlineDetail.setBackgroundColor( Integer.parseInt(not.getRenk()) );
        contentDetail.setBackgroundColor(  Integer.parseInt(not.getRenk()) );

        priorityDetail = ( TextView) findViewById(R.id.detailPriorityView);
        priorityDetail.setText("Öncelik:" + not.getOncelik());

        spinner = (Spinner) findViewById(R.id.detailPrioritySpinner);
        priorityAdapter = ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(priorityAdapter);
        spinner.setOnItemSelectedListener(this);

        colorButton = (Button) findViewById(R.id.detailColorButton);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(NoteDetail.this)
                        .setTitle("Renk Seç")
                        .initialColor(R.color.colorPrimary)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(NoteDetail.this,"Seçilen renk: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Seç", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                color = Integer.toString(selectedColor);
                                headlineDetail.setBackgroundColor(selectedColor);
                                contentDetail.setBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .build()
                        .show();
            }
        });

        dateViewDetail = (TextView) findViewById(R.id.detailDateView);
        dateAndTime = not.getTarih().split("\\s+")[0];
        dateViewDetail.setText(dateAndTime);

        updateButton = (Button) findViewById(R.id.detailUpdateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                //update the not object with new values
                not.setBaslik(headlineDetail.getText().toString());
                not.setIcerik(contentDetail.getText().toString());
                not.setAdres("-----Guncel adres-------");
                not.setOncelik(priority);
                not.setRenk(color);
                not.setTarih(isoFormat.format(new Date()));

                //update the not in database
                int result = db.updateRecord(not);

                //inform the user about process
                Toast.makeText(NoteDetail.this,"Not guncellendi, ID: " + not.getId(),Toast.LENGTH_SHORT).show();

                if( result == 0)
                    Log.d("UPDATE ERROR:", "NON UPDATED COLUMNS: " + result);

                //back to main activity
                Intent intent = new Intent(NoteDetail.this,MainActivity.class);
                startActivity(intent);
            }
        });


        deleteButton = (Button) findViewById(R.id.detailDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the note from database
                db.deleteRecord(not);
                Toast.makeText(NoteDetail.this,"Not silindi, ID: " + not.getId(),Toast.LENGTH_SHORT).show();

                //back to mainactivity
                Intent intent = new Intent(NoteDetail.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        if ( item.compareTo("Normal" ) < 0 ){
            priority = 1;
        }else
        if( item.compareTo("Normal") > 0 )
            priority = 3;
        else
            priority = 2;

        Log.d("-----spinner select--------",item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle bundle = intent.getExtras();
        if( bundle.containsKey("NOT") ) //if the intent comes from main activity
            not = bundle.getParcelable("NOT"); //get the not comes from main acrivity
        else{ // if the intent comes from notification
            id = bundle.getLong("NOTID"); //get the note id
            //Log.d("|ID",String.valueOf(id));
            not = db.getNote( id ); // then get the note from database with that id
        }
    }
}
