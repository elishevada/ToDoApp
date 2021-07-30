package com.elishevada.ex3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    public static final String MY_DB_NAME = "TodosDB.db";
    private SharedPreferences sp;

    private SQLiteDatabase contactsDB = null;
    private Button loginbtn;
    private EditText edtUserName, edtPassword;
    private boolean userflag=false,passflag=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("mylog", ">> oncreate()loginActivity");

        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String userloged = sp.getString("username", null);
        if(userloged!=null)
            jumpToSecondActivity();


        setTitle("Todo Login");

        edtUserName = findViewById(R.id.edtUserNameID);
        edtPassword = findViewById(R.id.edtPasswordID);
        loginbtn=findViewById(R.id.btnLoginID);

        loginbtn.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.btnLoginID)
                loginToUser();
        }
    };



    private void loginToUser() {
        userflag=false;
        passflag=false;

        try
        {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            contactsDB = openOrCreateDatabase(MY_DB_NAME, MODE_PRIVATE, null);

            // build an SQL statement to create 'users' table (if not exists)
            String sql = "CREATE TABLE IF NOT EXISTS users (username VARCHAR primary key, password VARCHAR);";
            contactsDB.execSQL(sql);
        }
        catch (Exception e)
        {
            Log.d("debug", "Error Creating Database");
        }


// Get the contact user
        String contactUser = edtUserName.getText().toString();
        String contactPass = edtPassword.getText().toString();
        if(contactUser.equals("")) {
            userflag = true;
            Toast.makeText(this, "missing username!", Toast.LENGTH_SHORT).show();
        }
        if(contactPass.equals("")) {
            passflag = true;
            Toast.makeText(this, "missing password !", Toast.LENGTH_SHORT).show();
        }

        if(passflag||userflag)
            return;


        String sql1 = "SELECT * FROM users WHERE username = '" + contactUser + "';";

        Cursor cursor = contactsDB.rawQuery(sql1, null);
        if(cursor.moveToFirst()) {
            String passwordc = cursor.getString(cursor.getColumnIndex("password"));
            if (passwordc.equals(contactPass)) {
                sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("username", contactUser);
                editor.commit();
                cursor.close();
                jumpToSecondActivity();
            }
            else{
                Toast.makeText(this, "password wrong!", Toast.LENGTH_SHORT).show();
            }
        }

        else {
            // Execute SQL statement to insert new data
            String sql = "INSERT INTO users (username, password) VALUES ('" + contactUser + "', '" + contactPass + "');";
            contactsDB.execSQL(sql);
            Toast.makeText(this, contactUser + " was insert!", Toast.LENGTH_SHORT).show();
            sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username", contactUser);
            editor.commit();
            cursor.close();
            jumpToSecondActivity();
        }

    }


    // Jump to Second Activity using Intent & startActivity
    public void jumpToSecondActivity()
    {
        Intent intent = new Intent(LoginActivity.this, ToDoListActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        // Set 3 Dots Menu on activity action bar
        MenuItem aboutMenu = menu.add("About");

        aboutMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                Log.d("mylog",">>> About Menu Clicked");
                showAboutDialog();
                return true;
            }
        });
        return true;
    }



    // Show AlertDialog to about app
    private void showAboutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        dialog.setIcon(R.drawable.ic_about);
        dialog.setTitle("About App");
        dialog.setMessage("ToDoApp (com.elishevada.ex3)" + "\n\n" + "By Elisheva Dayan, 25/05/2021.");
        dialog.setCancelable(false);
        dialog.setNegativeButton("ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d("mylog",">>> ok, got it!");
                dialog.dismiss();  // close the dialog
            }
        });
        dialog.show();
    }



    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("mylog", ">> onStart()MainActivity");
        String userloged = sp.getString("username", null);
        if(userloged!=null) {
            Toast.makeText(this, "To insert a new usename please log out", Toast.LENGTH_SHORT).show();
            jumpToSecondActivity();
        }
    }


}