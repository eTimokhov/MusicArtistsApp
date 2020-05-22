package com.example.musicartistsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.musicartistsapp.databinding.FilterFragmentBinding

class FilterFragment : DialogFragment(), View.OnClickListener {
    internal interface FilterListener {
        fun onFilter(filter: FilterModel?)
    }

    private var filterFragmentBinding: FilterFragmentBinding? = null
    private var filterListener: FilterListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        filterFragmentBinding = FilterFragmentBinding.inflate(inflater, container, false)
        filterFragmentBinding?.buttonSearch?.setOnClickListener(this)
        filterFragmentBinding?.buttonCancel?.setOnClickListener(this)
        return filterFragmentBinding?.getRoot()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterFragmentBinding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            filterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun onSearchClicked() {
        if (filterListener != null) {
            filterListener!!.onFilter(createFilter())
        }
        dismiss()
    }

    private fun retrieveSelectedCountry(): String? {
        val selectedCountry = filterFragmentBinding!!.spinnerCountry.selectedItem as String
        return if (getString(R.string.any) == selectedCountry) null else selectedCountry
    }

    private fun retrieveSelectedGenre(): String? {
        val selectedGenre = filterFragmentBinding!!.sprinnerGenre.selectedItem as String
        return if (getString(R.string.any) == selectedGenre) null else selectedGenre
    }

    private fun createFilter(): FilterModel {
        val filter = FilterModel()
        filter.country = retrieveSelectedCountry()
        filter.genre = retrieveSelectedGenre()
        return filter
    }

    fun onCancelClicked() {
        dismiss()
    }

    fun setDefaultSelection() {
        if (filterFragmentBinding != null) {
            filterFragmentBinding!!.spinnerCountry.setSelection(0)
            filterFragmentBinding!!.sprinnerGenre.setSelection(0)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
        }
    }
}