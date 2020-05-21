package com.example.musicartistsapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.musicartistsapp.databinding.FilterFragmentBinding;

public class FilterFragment extends DialogFragment implements View.OnClickListener {

    interface FilterListener {

        void onFilter(FilterModel filter);
    }
    private FilterFragmentBinding filterFragmentBinding;

    private FilterListener filterListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        filterFragmentBinding = FilterFragmentBinding.inflate(inflater, container, false);

        filterFragmentBinding.buttonSearch.setOnClickListener(this);
        filterFragmentBinding.buttonCancel.setOnClickListener(this);

        return filterFragmentBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filterFragmentBinding = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            filterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //why this code exists?
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (filterListener != null) {
            filterListener.onFilter(createFilter());
        }
        dismiss();
    }

    private String retrieveSelectedCountry() {
        String selectedCountry = (String) filterFragmentBinding.spinnerCountry.getSelectedItem();
        return getString(R.string.any).equals(selectedCountry) ? null : selectedCountry;
    }

    private String retrieveSelectedGenre() {
        String selectedGenre = (String) filterFragmentBinding.sprinnerGenre.getSelectedItem();
        return getString(R.string.any).equals(selectedGenre) ? null : selectedGenre;
    }

    private FilterModel createFilter() {
        FilterModel filter = new FilterModel();
        filter.setCountry(retrieveSelectedCountry());
        filter.setGenre(retrieveSelectedGenre());
        return filter;
    }

    public void onCancelClicked() {
        dismiss();
    }

    public void setDefaultSelection() {
        if (filterFragmentBinding != null) {
            filterFragmentBinding.spinnerCountry.setSelection(0);
            filterFragmentBinding.sprinnerGenre.setSelection(0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                onSearchClicked();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }
}
