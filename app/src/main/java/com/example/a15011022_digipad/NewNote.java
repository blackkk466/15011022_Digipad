package com.example.a15011022_digipad;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;


import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class NewNote extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText headline,content;
    private Button saveButton, colorButton;
    private Spinner spinner;
    private TextView dateView,dateValue,hourView,hourValue;
    private ArrayAdapter<CharSequence> priorityAdapter;

    private Not not;
    private int priority;
    private String color;
    private String currDateAndTime;
    private String address;


    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_note);

        //database connection
        db = new DatabaseHelper(this);

        color = String.valueOf(Color.WHITE);
        headline = (EditText) findViewById(R.id.newNoteHeadline);
        content = (EditText) findViewById(R.id.newNoteContent);

        //choose the priority with spinner Yüksek->3 Düşük->1
        spinner = (Spinner) findViewById(R.id.prioritySpinner);
        priorityAdapter = ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(priorityAdapter);
        spinner.setOnItemSelectedListener(this);

        colorButton = (Button) findViewById(R.id.colorButton);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //picking color for note background
                ColorPickerDialogBuilder
                        .with(NewNote.this)
                        .setTitle("Renk Seç")
                        .initialColor(R.color.colorPrimary)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(NewNote.this,"Seçilen renk: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Seç", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                color = Integer.toString(selectedColor);
                                headline.setBackgroundColor(selectedColor);
                                content.setBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                headline.setBackgroundColor(Color.WHITE);
                                content.setBackgroundColor(Color.WHITE);
                            }
                        })
                        .build()
                        .show();
            }
        });



        saveButton = (Button) findViewById(R.id.saveButtonNewNote);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if headline or content has zero length, can't store the note
                if( headline.getText().toString().length() == 0 || content.getText().toString().length() == 0 ){
                    Toast.makeText(NewNote.this,"Başlık ya da içerik boş olmamalı!",Toast.LENGTH_LONG).show();
                    return;
                }


                //isoFormat to store date and time in SQLite
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                currDateAndTime = isoFormat.format(new Date());


                //TODO address information from location services
                address = "Gecici"; //**************************************

                //creating instance of a note to store in database
                not = new Not(headline.getText().toString(),address,content.getText().toString(),color,priority,currDateAndTime);

                //insert note to database
                long result = db.insertRecod(not);

                if( result == -1 ) {// if failed to insert to databse, return
                    Toast.makeText(NewNote.this,"Veritabanına erişim esnasında hata!",Toast.LENGTH_SHORT).show();
                    return;
                }
                //if inserted succesfully save the note id for further use
                not.setId(result);

                //if alarm date and time are set, create alarm to send notification
                if( dateValue.getText().toString().length() != 0 && hourValue.getText().toString().length() != 0 )
                    createAlarm();

                //inform the user about succesfull insertion
                Toast.makeText(NewNote.this,"Not veritabanına kaydedildi, ID: " + String.valueOf(result),Toast.LENGTH_SHORT).show();

                //return to mainactivity
                Intent intent = new Intent(NewNote.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //picking date for alarm
        dateValue = (TextView) findViewById(R.id.reminderDateValue);
        dateView = (TextView) findViewById(R.id.reminderDateView);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar takvim = Calendar.getInstance();
                int yil = takvim.get(Calendar.YEAR);
                int ay = takvim.get(Calendar.MONTH);
                int gun = takvim.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dpd = new DatePickerDialog(NewNote.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month += 1;
                                dateValue.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, yil, ay, gun);

                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();
            }
        });

        //picking time for alarm
        hourValue = (TextView) findViewById(R.id.reminderHourValue);
        hourView = (TextView) findViewById(R.id.reminderHourView);

        hourView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);//Güncel saati aldık
                int minute = mcurrentTime.get(Calendar.MINUTE);//Güncel dakikayı aldık
                TimePickerDialog timePicker;


                timePicker = new TimePickerDialog(NewNote.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hourValue.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                timePicker.setTitle("Saat Seçiniz");
                timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
                timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", timePicker);

                timePicker.show();

            }
        });


    }

    //creating alarm and pendingIntent to pass the data to pass the data to broadcast receiver
    public void createAlarm(){
        Intent alarmIntent = new Intent(NewNote.this,MyReceiver.class);
        alarmIntent.putExtra("title",not.getBaslik());
        alarmIntent.putExtra("content",not.getIcerik());
        alarmIntent.putExtra("id",not.getId());

        AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(),2525, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //split the time, like 12:10 -> {"12","10"}
        String[] splitTime = hourValue.getText().toString().split(":");
        int hour = Integer.parseInt( splitTime[0] );
        int min = Integer.parseInt( splitTime[1] );

        //split the date, like 15/05/2005 -> {"15","05","2005"}
        String[] splitDate = dateValue.getText().toString().split("/");
        int day = Integer.parseInt(splitDate[0]);
        int month = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[2]);


        Calendar cal = Calendar.getInstance();
        //set the alarm date and time
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.DAY_OF_MONTH,day);
        cal.set(Calendar.MONTH,month-1);
        cal.set(Calendar.YEAR,year);


        //set the alarm and use RTC_WAKEUP to ensure the notification goes off even if the phone is locked
        aManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pIntent);
        //aManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+3000,pIntent);

        //Log.d("---------CALENDAR-->",cal.toString() );
        //DEBUG
        //SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //currDateAndTime = isoFormat.format(new Date());
        //Log.d("tarih:",currDateAndTime );

    }

    //select the priority, as the highest number is the most prior one
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

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        priority = 2;
    }
}
