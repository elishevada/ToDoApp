package com.elishevada.ex3;

public class Task {

    private int task_id;
    private String taskTitle;
    private String taskDescription;
    private long taskDatetime;


    public Task(int _id,String title, String description, long datetime)
    {
        task_id=_id;
        taskTitle = title;
        taskDescription = description;
        taskDatetime = datetime;

    }

    public int getTask_id(){
        return task_id;
    }

    public void setTask_id(int _id) {
        this.task_id = _id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String title) {
        this.taskTitle = title;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String descriptipn) {
        this.taskDescription = descriptipn;
    }

    public Long getTaskDatetime()
    {
        return taskDatetime;
    }

    public void setTaskDatetime(Long datetime)
    {
        this.taskDatetime = datetime;
    }



}
