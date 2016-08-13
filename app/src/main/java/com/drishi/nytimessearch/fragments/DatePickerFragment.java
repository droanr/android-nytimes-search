package com.drishi.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.DatePicker;

import com.drishi.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by drishi on 8/12/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    String dateChosen;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragFilters);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public interface DatePickerDialogListener {
        void onFinishEditDialog(String inputDate);
    }

    public void sendBackResult() {
        FragmentManager fm = getFragmentManager();
        DatePickerDialogListener listener = (DatePickerDialogListener) getTargetFragment();
        listener.onFinishEditDialog(dateChosen);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        SimpleDateFormat fm = new SimpleDateFormat("MMMM/dd/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        dateChosen = fm.format(calendar.getTime());
        sendBackResult();
    }
}
