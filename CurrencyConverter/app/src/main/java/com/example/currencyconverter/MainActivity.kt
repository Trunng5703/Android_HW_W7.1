// MainActivity.kt
package com.example.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner

    // Track which EditText is currently active (source)
    private var isFirstEditTextSource = true

    // Sample exchange rates (in practice, these would come from an API)
    private val exchangeRates = mapOf(
        "EUR" to 1.0,
        "VND" to 27369.1043
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editText1 = findViewById(R.id.editText1)
        editText2 = findViewById(R.id.editText2)
        spinner1 = findViewById(R.id.spinner1)
        spinner2 = findViewById(R.id.spinner2)

        // Setup currency spinners
        val currencies = arrayOf("EUR", "VND")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner1.adapter = adapter
        spinner2.adapter = adapter

        // Set default selections
        spinner1.setSelection(0) // EUR
        spinner2.setSelection(1) // VND

        // Setup EditText focus listeners
        editText1.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isFirstEditTextSource = true
        }

        editText2.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isFirstEditTextSource = false
        }

        // Setup text change listeners
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Prevent recursive calls
                if (s?.hashCode() == editText1.text.hashCode() && isFirstEditTextSource ||
                    s?.hashCode() == editText2.text.hashCode() && !isFirstEditTextSource) {
                    updateConversion()
                }
            }
        }

        editText1.addTextChangedListener(textWatcher)
        editText2.addTextChangedListener(textWatcher)

        // Setup spinner selection listeners
        spinner1.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateConversion()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        spinner2.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateConversion()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun updateConversion() {
        try {
            val sourceCurrency = if (isFirstEditTextSource) spinner1.selectedItem.toString() else spinner2.selectedItem.toString()
            val targetCurrency = if (isFirstEditTextSource) spinner2.selectedItem.toString() else spinner1.selectedItem.toString()

            val sourceEditText = if (isFirstEditTextSource) editText1 else editText2
            val targetEditText = if (isFirstEditTextSource) editText2 else editText1

            val sourceAmount = sourceEditText.text.toString().toDoubleOrNull() ?: 0.0

            // Calculate conversion
            val sourceRate = exchangeRates[sourceCurrency] ?: 1.0
            val targetRate = exchangeRates[targetCurrency] ?: 1.0
            val result = sourceAmount * targetRate / sourceRate

            // Format result
            val df = DecimalFormat("#,##0.00")
            targetEditText.setText(df.format(result))
        } catch (e: Exception) {
            // Handle any errors
            if (isFirstEditTextSource) {
                editText2.setText("0")
            } else {
                editText1.setText("0")
            }
        }
    }
}