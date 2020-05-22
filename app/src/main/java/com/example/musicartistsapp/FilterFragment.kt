package com.example.musicartistsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.musicartistsapp.databinding.FilterFragmentBinding
import java.lang.IllegalStateException

class FilterFragment : DialogFragment(), View.OnClickListener {
    interface FilterListener {
        fun onFilter(filter: FilterModel)
    }

    private lateinit var filterFragmentBinding: FilterFragmentBinding
    private lateinit var filterListener: FilterListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        filterFragmentBinding = FilterFragmentBinding.inflate(inflater, container, false)

        filterFragmentBinding.buttonSearch.setOnClickListener(this)
        filterFragmentBinding.buttonCancel.setOnClickListener(this)
        return filterFragmentBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            filterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window ?: throw IllegalStateException("Cannot retrieve dialog window")
        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun onSearchClicked() {
        filterListener.onFilter(createFilter())
        dismiss()
    }

    private fun retrieveSelectedCountry(): String? {
        val selectedCountry = filterFragmentBinding.spinnerCountry.selectedItem as String
        return if (getString(R.string.any) == selectedCountry) null else selectedCountry
    }

    private fun retrieveSelectedGenre(): String? {
        val selectedGenre = filterFragmentBinding.sprinnerGenre.selectedItem as String
        return if (getString(R.string.any) == selectedGenre) null else selectedGenre
    }

    private fun createFilter(): FilterModel {
        val filter = FilterModel()
        filter.country = retrieveSelectedCountry()
        filter.genre = retrieveSelectedGenre()
        return filter
    }

    private fun onCancelClicked() {
        dismiss()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
        }
    }
}