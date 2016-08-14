package com.drishi.nytimessearch.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.drishi.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by drishi on 8/11/16.
 */
public class FiltersFragment extends DialogFragment implements DatePickerFragment.DatePickerDialogListener, AdapterView.OnItemSelectedListener {

    TextView tvBeginDate;
    Spinner spSortOrder;
    ArrayList<String> sortOptions;
    ArrayAdapter<String> adapter;
    int startingSortOrder;
    Button btnSaveFilters;

    Date beginDate;
    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;

    public FiltersFragment() {

    }

    public interface FiltersFragmentListener {
        void onSaveFilters(ArrayList<String> filters, String sort, Date date);
    }

    public static FiltersFragment newInstance(String title, String date, String order, ArrayList<String> filters) {
        FiltersFragment fragment = new FiltersFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("date", date);
        args.putString("order", order);
        args.putStringArrayList("filters", filters);
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

        String date = getArguments().getString("date");
        if (date.length() != 0) {
            tvBeginDate.setText(getArguments().getString("date"));
        } else {
            tvBeginDate.setText("__________________");
        }

        ArrayList<String> filters = getArguments().getStringArrayList("filters");

        if (filters.contains("Arts")) {
            cbArts.setChecked(true);
        }

        if (filters.contains("Fashion & Style")) {
            cbFashion.setChecked(true);
        }

        if (filters.contains("Sports")) {
            cbSports.setChecked(true);
        }

        String sort = getArguments().getString("order");

        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.sort_options_array));

        spSortOrder.setAdapter(adapter);
        startingSortOrder = adapter.getPosition(sort);
    }

    public void setUpViews(View view) {
        tvBeginDate = (TextView) view.findViewById(R.id.tvBeginDate);
        spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        sortOptions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.sort_options_array)));
        cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        cbFashion = (CheckBox) view.findViewById(R.id.cbFashion);
        cbSports = (CheckBox) view.findViewById(R.id.cbSports);
        btnSaveFilters = (Button) view.findViewById(R.id.btnSaveFilters);

        tvBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the DateDialogFragment
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTargetFragment(FiltersFragment.this, 300);
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        spSortOrder.setOnItemSelectedListener(this);
        btnSaveFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FiltersFragmentListener listener = (FiltersFragmentListener) getActivity();
                ArrayList<String> filters = new ArrayList<>();
                if(cbArts.isChecked()) {
                    filters.add("Arts");
                }
                if(cbFashion.isChecked()) {
                    filters.add("Fashion & Style");
                }
                if(cbSports.isChecked()) {
                    filters.add("Sports");
                }

                listener.onSaveFilters(filters, spSortOrder.getSelectedItem().toString(), beginDate);
                dismiss();
            }
        });
    }

    @Override
    public void onFinishEditDate(Date inputDate) {
        beginDate = inputDate;
        tvBeginDate.setText(new SimpleDateFormat("MMMM dd, yyyy").format(beginDate));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spSortOrder.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        spSortOrder.setSelection(startingSortOrder);
    }
}
