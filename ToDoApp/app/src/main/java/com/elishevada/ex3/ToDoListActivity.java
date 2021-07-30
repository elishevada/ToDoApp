package com.elishevada.ex3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;


public class ToDoListActivity extends AppCompatActivity implements ListView.OnItemClickListener,AdapterView.OnItemLongClickListener,SearchView.OnQueryTextListener {
    private SharedPreferences sp;
    private FloatingActionButton plus;
    private ListView listView;
    private ArrayList<Task> tasksList;
    private  String userloged;
    private SQLiteDatabase contactsDB = null;
    private boolean updateflag;
    private  TaskAdapter taskAdapter;
    private int idupdate;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);





        sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        userloged = sp.getString("username", null);
        setTitle("Todo List ("+userloged+")");

        updateflag=false;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("updateflag", updateflag);
        editor.commit();

        plus=findViewById(R.id.floatingActionButton2);
        plus.setOnClickListener(onClickListener);
        tasksList = new ArrayList<Task>();
        addTaskToList();
        // add Item Click Listener
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        searchView=findViewById(R.id.searchViewID);
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        filter(text);
        return false;
    }


    //find task that contains the test
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        tasksList.clear();
        if (charText.length() == 0) {
            runSqlandList();
        }
        else {
            String sql = "SELECT * FROM todos WHERE( description LIKE '%" + charText + "%' or title LIKE '%" + charText + "%') AND username = '" + userloged + "';";
            runcursor(sql);
        }
        taskAdapter.notifyDataSetChanged();
    }


    private void addTaskToList() {
        try
        {
            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            contactsDB = openOrCreateDatabase(LoginActivity.MY_DB_NAME, MODE_PRIVATE, null);

        }
        catch (Exception e)
        {
            Log.d("debug", "Error Creating Database");
        }

        runSqlandList();
        // Create an TaskAdapter, whose data source is a list of Task.
        // The adapter knows how to create list item views for each item in the list.
        taskAdapter = new TaskAdapter(this, tasksList);
//        attach the adapter to the listView.
        listView.setAdapter(taskAdapter);

    }


    public void runSqlandList(){
        // A Cursor provides read and write access to database results
        String sql1 = "SELECT * FROM todos WHERE username = '" + userloged + "';";
        runcursor(sql1);

    }

    public void runcursor(String sql1){
        Cursor cursor = contactsDB.rawQuery(sql1, null);

        // Get the index for the column name provided
        int idColumn = cursor.getColumnIndex("id");
        int taskTitlecol = cursor.getColumnIndex("title");
        int taskDescriptioncol = cursor.getColumnIndex("description");
        int  taskDatetimecol=cursor.getColumnIndex("datetime");

        // Move to the first row of results & Verify that we have results
        if (cursor.moveToFirst()) {
            do {
                // Get the results and store them in a String
                int id = cursor.getInt(idColumn);
                String taskTitle = cursor.getString(taskTitlecol);
                String taskDescription = cursor.getString(taskDescriptioncol);
                long taskDatetime=cursor.getLong(taskDatetimecol);
                tasksList.add(new Task(id,taskTitle,taskDescription,taskDatetime));

                // Keep getting results as long as they exist
            } while (cursor.moveToNext());
            cursor.close();
        } else
            Toast.makeText(this, "No Results to Show", Toast.LENGTH_SHORT).show();

        // Get a reference to the ListView
        listView = findViewById(R.id.listViewID);
    }




    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.floatingActionButton2)
                jumpToThirdActivity(v);
        }
    };

    public void jumpToThirdActivity(View v){
        Intent intent = new Intent(ToDoListActivity.this, EditorActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_one) {
            sp = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.commit();
            jumpToFirstActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void jumpToFirstActivity(){
        Intent intent = new Intent(ToDoListActivity.this, LoginActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        showDeleteDialog(position);
        return true;
    }


    // Show AlertDialog to delete app
    private void showDeleteDialog(int position)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.ic_delete);
        dialog.setTitle("Delete Task");
        dialog.setMessage("Do you really want to delete task?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String sql = "DELETE FROM todos WHERE id = '" + tasksList.get(position).getTask_id() + "';";
                contactsDB.execSQL(sql);
                tasksList.clear();
                runSqlandList();
                taskAdapter.notifyDataSetChanged();
                postdel();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();  // close the dialog
            }
        });
        dialog.show();
    }

    public void postdel(){
        Toast.makeText(this, "Todo was DELETED", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = tasksList.get(position);
        idupdate = task.getTask_id();
        updateflag = true;
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("updateflag", updateflag);
        editor.putInt("idupdate", idupdate);
        editor.commit();
        jumpToThirdActivity(view);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("mylog", ">> onStart()todolistactivity");
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("updateflag", false);
        editor.commit();
    }
}