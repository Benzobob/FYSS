package com.fyss.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyss.R;
import com.fyss.controller.SyMeetingPageActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

public class PushReminder {
    AppCompatActivity activity;

    public PushReminder() {
    }

    public PushReminder( String location, Date Start_date, Date end_date, AppCompatActivity ac) {
        // TODO Auto-generated constructor stub
        activity=ac;
        addReminder( location, Start_date,end_date);
    }



    private void addReminder(String location, Date start, Date end) {
        ContentResolver cr=activity.getContentResolver();

        ContentValues calEvent = new ContentValues();
        calEvent.put(CalendarContract.Events.CALENDAR_ID, 1);
        calEvent.put(CalendarContract.Events.TITLE, "Group Meeting");
        calEvent.put(CalendarContract.Events.DTSTART, start.getTime());
        calEvent.put(CalendarContract.Events.EVENT_LOCATION, location);
        calEvent.put(CalendarContract.Events.DTEND, end.getTime());
        calEvent.put(CalendarContract.Events.HAS_ALARM, 1);
        calEvent.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);
        Uri uri =cr.insert(CalendarContract.Events.CONTENT_URI, calEvent);

        int id = Integer.parseInt(uri.getLastPathSegment());

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID,id);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 30);

        Uri uri2 = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        Snackbar mySnackbar = Snackbar.make(activity.findViewById(android.R.id.content),"Reminder Set.", Snackbar.LENGTH_LONG);
        mySnackbar.setAction("See Calendar", new goToCalendarListener(start));
        mySnackbar.show();
    }

    public class goToCalendarListener implements View.OnClickListener {
        private Date startDate;
        public goToCalendarListener(Date start){
            startDate = start;
        }

        @Override
        public void onClick(View v) {
            Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            ContentUris.appendId(builder, startDate.getTime());
            Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(builder.build());
            v.getContext().startActivity(intent);
        }
    }
}
