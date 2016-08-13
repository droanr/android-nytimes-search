package com.drishi.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.drishi.nytimessearch.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by drishi on 8/11/16.
 */
public class FiltersFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener {

    TextView tvBeginDate;
    Spinner spSortOrder;
    ArrayList<String> sortOptions;

    public FiltersFragment() {

    }

    public static FiltersFragment newInstance(String title, String date, String order) {
        FiltersFragment fragment = new FiltersFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("date", date);
        args.putString("order", order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filters, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);

        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        tvBeginDate.setText(getArguments().getString("date"));

        String sortOrder = getArguments().getString("order");
        spSortOrder.setSelection(sortOptions.indexOf(sortOrder));

    }

    public void setUpViews(View view) {
        tvBeginDate = (TextView) view.findViewById(R.id.tvBeginDate);
        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        sortOptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sort_options_array)));

        tvBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the DateDialogFragment
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTargetFragment(FiltersFragment.this, 300);
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        tvBeginDate.setText(inputText);
    }

}
