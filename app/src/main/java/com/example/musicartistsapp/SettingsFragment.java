package com.example.musicartistsapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.musicartistsapp.databinding.FilterFragmentBinding;
import com.example.musicartistsapp.databinding.SettingsFragmentBinding;

public class SettingsFragment extends DialogFragment implements View.OnClickListener {

    private SettingsFragmentBinding settingsFragmentBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsFragmentBinding = SettingsFragmentBinding.inflate(inflater, container, false);

        settingsFragmentBinding.settingsSave.setOnClickListener(this);
        settingsFragmentBinding.settingsCancel.setOnClickListener(this);

        return settingsFragmentBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingsFragmentBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_save:
                onSettingsSaveClicked();
                break;
            case R.id.settings_cancel:
                onSettingsCancelClicked();
                break;
        }
    }

    private void onSettingsCancelClicked() {
        dismiss();
    }

    private void onSettingsSaveClicked() {
        String selectedFontFamily = (String) settingsFragmentBinding.spinnerFontFamily.getSelectedItem();
        String selectedFontSize = (String) settingsFragmentBinding.spinnerFontSize.getSelectedItem();
        String selectedBackgroundColor = (String) settingsFragmentBinding.spinnerBackgroundColor.getSelectedItem();

        GlobalConfig.getInstance().updateGlobalConfig(selectedFontFamily, selectedFontSize, selectedBackgroundColor);

        dismiss();
    }
}
