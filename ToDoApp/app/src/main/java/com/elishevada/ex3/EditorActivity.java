package com.elishevada.ex3;




import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditorActivity extends AppCompatActivity {
    private EditText title,description,date,time;
    private Button add, btnDatePicker, btnTimePicker;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private boolean titleflag=false,timeflag=false,dateflag=false,descflag=false,datevalidflag=false,timevalidflag=false;
    private SQLiteDatabase contactsDB1 = null;
    private SharedPreferences sp;
    private TextView update;
    private boolean updateflag;
    private int updateid;
    private static final int ALARM_ID = 111;
    private AlarmManager alarmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        setTitle("Todo Editor");

        // Get the System Alarm Manager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        title = findViewById(R.id.titleID);
        description = findViewById(R.id.descriptionID);
        date = findViewById(R.id.dateID);
        time = findViewById(R.id.timeID);

        add=findViewById(R.id.buttonAddID);
        add.setOnClickListener(onClickListener);

        btnDatePicker=(Button)findViewById(R.id.btnDateId);
        btnTimePicker=(Button)findViewById(R.id.btnTimeId);

        btnDatePicker.setOnClickListener(onClickListener);
        btnTimePicker.setOnClickListener(onClickListener);

        update = (TextView)findViewById(R.id.addorupdate);

        try
        {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            contactsDB1=openOrCreateDatabase(LoginActivity.MY_DB_NAME,MODE_PRIVATE,null);

            // build an SQL statement to create 'users' table (if not exists)
            String sql = "CREATE TABLE IF NOT EXISTS todos (id integer primary key,username VARCHAR, title VARCHAR,description VARCHAR,datetime LONG);";
            contactsDB1.execSQL(sql);
        }
        catch (Exception e)
        {
            Log.d("debug", "Error Creating Database");
        }

        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        updateflag = sp.getBoolean("updateflag", false);
        updateid=sp.getInt("idupdate",0);
        if(updateflag)
            updateTask();


    }

    private void updateTask() {
        update.setText("UPDATE Todo (id= "+updateid+")");
        add.setText("UPDATE");

        String sql1 = "SELECT * FROM todos WHERE id = '" + updateid + "';";
        Cursor cursor = contactsDB1.rawQuery(sql1, null);

        // Get the index for the column name provided
        int taskTitlecol = cursor.getColumnIndex("title");
        int taskDescriptioncol = cursor.getColumnIndex("description");
        int  taskDatetimecol=cursor.getColumnIndex("datetime");


        // Move to the first row of results & Verify that we have results
        if (cursor.moveToFirst()) {

            // Get the results and store them in a String
            String taskTitle = cursor.getString(taskTitlecol);
            String taskDescription = cursor.getString(taskDescriptioncol);
            long taskDatetime=cursor.getLong(taskDatetimecol);

            SimpleDateFormat SDFormat1= new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat SDFormat2= new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            // Get the description from the current Task object
            calendar.setTimeInMillis(taskDatetime);
            String datefromDB =SDFormat1.format(calendar.getTime());
            String timefromDB =SDFormat2.format(calendar.getTime());

            title.setText(taskTitle);
            description.setText(taskDescription);
            date.setText(datefromDB);
            time.setText(timefromDB);

            cursor.close();
        } else
            Toast.makeText(this, "cant find details", Toast.LENGTH_SHORT).show();


    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.buttonAddID)
                addOrUpdateTaskToList(v);
            if (v.getId() ==R.id.btnDateId) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
            if (v.getId() ==R.id.btnTimeId) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditorActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        }
    };




    public void addOrUpdateTaskToList(View v) {
        titleflag=false;
        descflag=false;
        dateflag=false;
        timeflag=false;
        timevalidflag=false;
        datevalidflag=false;

        String titlec = title.getText().toString();
        String descc = description.getText().toString();
        String datec = date.getText().toString();
        String timec = time.getText().toString();

        //empty lines
        if (titlec.equals("")) {
            titleflag = true;
            Toast.makeText(this, "missing title!", Toast.LENGTH_SHORT).show();
        }
        if (descc.equals("")) {
            descflag = true;
            Toast.makeText(this, "missing description !", Toast.LENGTH_SHORT).show();
        }
        if (datec.equals("")) {
            dateflag = true;
            Toast.makeText(this, "missing date!", Toast.LENGTH_SHORT).show();
        }
        if (timec.equals("")) {
            timeflag = true;
            Toast.makeText(this, "missing time !", Toast.LENGTH_SHORT).show();
        }

        //valid time
        SimpleDateFormat SDFormatt = new SimpleDateFormat("HH:mm");
        SDFormatt.setLenient(false);
        try {
            SDFormatt.parse(timec);
        } catch (ParseException excpt) {
            Toast.makeText(this, "time is not valid", Toast.LENGTH_SHORT).show();
            timevalidflag = true;
        }


        //valid date
        SimpleDateFormat SDFormatd = new SimpleDateFormat("dd/MM/yyyy");
        SDFormatt.setLenient(false);
        try {
            SDFormatd.parse(datec);
        } catch (ParseException excpt) {
            Toast.makeText(this, "date is not valid", Toast.LENGTH_SHORT).show();
            datevalidflag = true;
        }

        if (titleflag || descflag || dateflag || timeflag || datevalidflag || timevalidflag)
            return;


        //convert date and time to a long number
        SimpleDateFormat SDFormat = new SimpleDateFormat("dd/MM/yyyyHH:mm");
        String stringmerge = datec + timec;

        // Initializing the calender Object
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(SDFormat.parse(stringmerge));
        } catch (ParseException excpt) {
            excpt.printStackTrace();
        }

        long datetime = cal.getTimeInMillis();
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("longdatetime", datetime);
        editor.commit();

        //get the current date and time
        Calendar calendernow = Calendar.getInstance();
        long currenttimeanddate = calendernow.getTimeInMillis();

        //UPDATE
        if (updateflag) {
            String sql = "UPDATE todos SET title ='" + titlec +"', description = '" + descc + "', dateTime = '" + datetime + "' WHERE id = '" + updateid + "';";
            contactsDB1.execSQL(sql);
            Toast.makeText(this, " Todo was UPDATED", Toast.LENGTH_SHORT).show();
            //check if time is not past
            if(datetime >= currenttimeanddate) {
                Log.d("mylog", "date is future from update");
                createOneTimeAlarm();
            }
            jumpToSecondActivity(v);

            //ADD
        } else {
            //get  the current user to put at the task
            sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String userloged = sp.getString("username", null);

            String sql = "INSERT INTO todos (username, title,description,datetime) VALUES ('" + userloged + "', '" + titlec + "', '" + descc + "', '" + datetime + "');";
            //         Execute SQL statement to insert new data
            contactsDB1.execSQL(sql);
            Toast.makeText(this, "Todo was ADDED", Toast.LENGTH_SHORT).show();
            //check if time is not past
            if(datetime >= currenttimeanddate) {
                Log.d("mylog", "date is future from add");
                createOneTimeAlarm();
            }
            jumpToSecondActivity(v);
        }
    }

    public void jumpToSecondActivity(View v){
        Intent intent = new Intent(EditorActivity.this, ToDoListActivity.class);
        startActivity(intent);
    }


    // Create OneTime Alarm will fire event in 10 sec from now
    public void createOneTimeAlarm()
    {
        Log.d("mylog", "createOneTimeAlarm()1111111111111111");



        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String userloged = sp.getString("username", null);
        // Create Intent to call the BroadcastReceiver
        Intent alarmIntent = new Intent(this, AlarmClockReceiver.class);
        alarmIntent.putExtra("username",userloged);
        alarmIntent.putExtra("tasktitle", title.getText().toString());

        // Create PendingIntent to start the BroadcastReceiver when alarm is triggered.
        // Params:
        // 1. Context - activity context - this
        // 2. int - the id of the alarm - ALARM_ID
        // 3. Intent - intent - alarmIntent
        // 4. int - flag - PendingIntent.FLAG_UPDATE_CURRENT
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        long time=sp.getLong("longdatetime",0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("mylog", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, alarmPendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alarmPendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, alarmPendingIntent);


        Log.d("mylog", "createOneTimeAlarm()222222222222");
    }
}