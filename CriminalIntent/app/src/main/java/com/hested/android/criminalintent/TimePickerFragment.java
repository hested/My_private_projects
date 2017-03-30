package com.hested.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Project:  CriminalIntent
 * Package:  com.hested.android.criminalintent
 * Date:     16-03-2017
 * Time:     13:37
 * Author:   Johnni Hested
 */

public class TimePickerFragment extends AppCompatDialogFragment {
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private static final String ARG_DATE = "date";

    private TimePicker mTimePicker;
    private Calendar mCalendar;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setIs24HourView(true);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mCalendar.get(Calendar.YEAR);
                                int month = mCalendar.get(Calendar.MONTH);
                                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                                int hour = mTimePicker.getCurrentHour();
                                int minute = mTimePicker.getCurrentMinute();
                                Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                                sendResult(Activity.RESULT_OK, date);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date) {

        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
