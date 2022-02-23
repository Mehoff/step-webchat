package com.step.webchat;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {

    private final SimpleDateFormat dateFormat;

    private int id;
    private String text;
    private String author;
    private Date moment;

    public Message(int id, String text, String author, String momentText){
        this.id = id;
        this.text = text;
        this.author = author;

        this.dateFormat = new SimpleDateFormat("yyy-MM-dd hh:mm:ss", Locale.UK);
        setMoment(momentText);
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getText(){
        return text;
    }

    public String getAuthor(){
        return author;
    }

    public void setText(String text){
        this.text = text;
    }
    public void setAuthor(String author){
        this.author = author;
    }

    public Date getMoment() {
        return moment;
    }

    public String getDateString(){
        if(DateUtils.isToday(moment.getTime())){
            return new SimpleDateFormat("H:mm").format(moment); // 9:00
        } else {
            return new SimpleDateFormat("MM-dd H:mm").format(moment); // 02-15 9:00
        }
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public void setMoment(String momentText){
        try {
            this.moment = dateFormat.parse(momentText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
