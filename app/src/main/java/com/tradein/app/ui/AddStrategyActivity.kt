package com.tradein.app.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tradein.app.R
import com.tradein.app.database.DatabaseHelper
import com.tradein.app.model.DcaStrategy
import java.util.*

class AddStrategyActivity : AppCompatActivity() {

    private lateinit var tilAsset:     TextInputLayout
    private lateinit var tilAmount:    TextInputLayout
    private lateinit var tilNotes:     TextInputLayout
    private lateinit var etAsset:      TextInputEditText
    private lateinit var etAmount:     TextInputEditText
    private lateinit var etStartDate:  TextInputEditText
    private lateinit var etEndDate:    TextInputEditText
    private lateinit var etNotes:      TextInputEditText
    private lateinit var spinnerFreq:  Spinner
    private lateinit var btnSave:      MaterialButton
    private lateinit var dbHelper:     DatabaseHelper

    private var selectedFrequency = "Weekly"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_strategy)

        dbHelper = DatabaseHelper(this)
        bindViews()
        setupFrequencySpinner()
        setupDatePickers()

        supportActionBar?.apply {
            title = "New DCA Strategy"
            setDisplayHomeAsUpEnabled(true)
        }

        btnSave.setOnClickListener { saveStrategy() }
    }

    private fun bindViews() {
        tilAsset    = findViewById(R.id.til_asset)
        tilAmount   = findViewById(R.id.til_amount)
        tilNotes    = findViewById(R.id.til_notes)
        etAsset     = findViewById(R.id.et_asset_symbol)
        etAmount    = findViewById(R.id.et_investment_amount)
        etStartDate = findViewById(R.id.et_start_date)
        etEndDate   = findViewById(R.id.et_end_date)
        etNotes     = findViewById(R.id.et_notes)
        spinnerFreq = findViewById(R.id.spinner_frequency)
        btnSave     = findViewById(R.id.btn_save)
    }

    private fun setupFrequencySpinner() {
        val frequencies = listOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFreq.adapter = adapter
        spinnerFreq.setSelection(1)

        spinnerFreq.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedFrequency = frequencies[pos]
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupDatePickers() {
        etStartDate.setOnClickListener { showDatePicker { date -> etStartDate.setText(date) } }
        etEndDate.setOnClickListener   { showDatePicker { date -> etEndDate.setText(date)   } }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
                onDateSelected(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validate(): Boolean {
        var valid = true

        if (etAsset.text.isNullOrBlank()) {
            tilAsset.error = "Asset symbol is required"
            valid = false
        } else { tilAsset.error = null }

        if (etAmount.text.isNullOrBlank()) {
            tilAmount.error = "Investment amount is required"
            valid = false
        } else {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                tilAmount.error = "Enter a valid positive amount"
                valid = false
            } else { tilAmount.error = null }
        }

        if (etStartDate.text.isNullOrBlank()) {
            Snackbar.make(btnSave, "Please select a start date", Snackbar.LENGTH_SHORT).show()
            valid = false
        }

        if (etEndDate.text.isNullOrBlank()) {
            Snackbar.make(btnSave, "Please select an end date", Snackbar.LENGTH_SHORT).show()
            valid = false
        }

        if (valid && etStartDate.text.toString() >= etEndDate.text.toString()) {
            Snackbar.make(btnSave, "End date must be after start date", Snackbar.LENGTH_SHORT).show()
            valid = false
        }

        return valid
    }

    private fun saveStrategy() {
        if (!validate()) return

        val strategy = DcaStrategy(
            assetSymbol      = etAsset.text.toString().trim().uppercase(),
            investmentAmount = etAmount.text.toString().toDouble(),
            frequency        = selectedFrequency,
            startDate        = etStartDate.text.toString(),
            endDate          = etEndDate.text.toString(),
            notes            = etNotes.text.toString().trim()
        )

        val id = dbHelper.insertStrategy(strategy)
        if (id != -1L) {
            Toast.makeText(this, "Strategy saved!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Snackbar.make(btnSave, "Failed to save. Please try again.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
