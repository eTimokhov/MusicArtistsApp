package com.example.musicartistsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.musicartistsapp.GlobalConfig.Companion.GlobalConfigInstance
import com.example.musicartistsapp.databinding.SettingsFragmentBinding
import java.lang.IllegalStateException

class SettingsFragment : DialogFragment(), View.OnClickListener {
    private lateinit var settingsFragmentBinding: SettingsFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingsFragmentBinding = SettingsFragmentBinding.inflate(inflater, container, false)

        settingsFragmentBinding.settingsSave.setOnClickListener(this)
        settingsFragmentBinding.settingsCancel.setOnClickListener(this)
        return settingsFragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: throw IllegalStateException("Cannot retrieve dialog window")
        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settings_save -> onSettingsSaveClicked()
            R.id.settings_cancel -> onSettingsCancelClicked()
        }
    }

    private fun onSettingsCancelClicked() {
        dismiss()
    }

    private fun onSettingsSaveClicked() {
        val selectedFontFamily = settingsFragmentBinding.spinnerFontFamily.selectedItem as String
        val selectedFontSize = settingsFragmentBinding.spinnerFontSize.selectedItem as String
        val selectedBackgroundColor = settingsFragmentBinding.spinnerBackgroundColor.selectedItem as String
        GlobalConfigInstance.updateGlobalConfig(selectedFontFamily, selectedFontSize, selectedBackgroundColor)
        dismiss()
    }
}