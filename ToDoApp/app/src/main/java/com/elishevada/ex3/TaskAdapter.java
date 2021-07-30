package com.elishevada.ex3;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<Task>
{

    public TaskAdapter(Activity context, ArrayList<Task> tasksList)
    {
        // Here, we initialize the Task's internal storage for the context and the list.
        // the second argument is used when the Task is populating a single TextView.
        //  the adapter is not going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, tasksList);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if the existing view is being reused, otherwise inflate the view
        if(convertView == null)
        {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            convertView = View.inflate(getContext(), R.layout.list_item, null);
        }
        // Get the {@link AndroidFlavor} object located at this position in the list
        Task currentTask = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID title
        TextView title = convertView.findViewById(R.id.titleID);
        // Get the title from the current Task object and
        // set this text on the name TextView
        title.setText(currentTask.getTaskTitle());

        // Find the TextView in the list_item.xml layout with the ID description
        TextView description = convertView.findViewById(R.id.descriptionID);
        // Get the description from the current Task object and
        // set this text on the name TextView
        description.setText(currentTask.getTaskDescription());

        // Find the TextView in the list_item.xml layout with the ID description
        TextView datetime = convertView.findViewById(R.id.datetimeID);
        SimpleDateFormat SDFormat1= new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat SDFormat2= new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        // Get the description from the current Task object
        calendar.setTimeInMillis(currentTask.getTaskDatetime());
        String date =SDFormat1.format(calendar.getTime());
        String time =SDFormat2.format(calendar.getTime());
        datetime.setText(date + "\n           " + time);

        // Return the whole list item layout (containing 3 TextViews)
        // so that it can be shown in the ListView
        return convertView;
    }

}
