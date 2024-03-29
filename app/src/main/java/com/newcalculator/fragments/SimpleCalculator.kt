package com.newcalculator.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.newcalculator.R

class SimpleCalculator : AppCompatActivity() {

    private lateinit var displayTextView: TextView
    private lateinit var displayOperationTextView: TextView
    private var displayOperationTextViewSave: String = ""
    private var displayTextSave: String = ""
    private var firstNumber: String = ""
    private var secondNumber: String = ""
    private var operation: String = ""
    private var lastOperation: String = ""
    private var calculated: Boolean = false
    private var repeat: Boolean = false
    private var cleared: Boolean = false
    private var error: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_calculator)

        getSavedData(savedInstanceState)
        simpleCalculator()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            displayTextSave = displayTextView.text.toString()
            displayOperationTextViewSave = displayOperationTextView.text.toString()

            outState.putString("displayTextSave", displayTextSave)
            outState.putString("displayOperationTextViewSave", displayOperationTextViewSave)
            outState.putString("firstNumber", firstNumber)
            outState.putString("secondNumber", secondNumber)
            outState.putString("operation", operation)
            outState.putString("lastOperation", lastOperation)
            outState.putBoolean("calculated", calculated)
            outState.putBoolean("repeat", repeat)
            outState.putBoolean("cleared", cleared)
            outState.putBoolean("error", error)
        }
    }
    private fun getSavedData(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedInstanceState.let {
                displayTextSave = it.getString("displayTextSave", "")
                displayOperationTextViewSave = it.getString("displayOperationTextViewSave", "")
                firstNumber = it.getString("firstNumber", "")
                secondNumber = it.getString("secondNumber", "")
                operation = it.getString("operation", "")
                lastOperation = it.getString("lastOperation", "")
                calculated = it.getBoolean("calculated", false)
                repeat = it.getBoolean("repeat", false)
                cleared = it.getBoolean("cleared", false)
                error = it.getBoolean("error", false)
            }
            displayTextView = findViewById(R.id.displayTextView)
            displayTextView.text = displayTextSave

            displayOperationTextView = findViewById(R.id.displayTextViewOperation)
            displayOperationTextView.text = displayOperationTextViewSave
        }
    }

    private fun simpleCalculator() {
        displayTextView = findViewById(R.id.displayTextView)
        displayOperationTextView = findViewById(R.id.displayTextViewOperation)

        // IDs for number buttons
        val numberButtonsIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )

        val decimalButton = findViewById<Button>(R.id.decimalButton)
        decimalButton.setOnClickListener { appendDecimalPoint() }

        val operationButtonsIdsToOperation = mapOf(
            R.id.addButton to "+",
            R.id.subtractButton to "-",
            R.id.multiplyButton to "*",
            R.id.divideButton to "/",
        )

        numberButtonsIds.forEach { id ->
            findViewById<Button>(id)
                .setOnClickListener { appendNumber((it as Button).text.toString()) }
        }

        operationButtonsIdsToOperation.forEach { (id, operation) ->
            findViewById<Button>(id).setOnClickListener {
                prepareOperation(operation)
            }
        }

        findViewById<Button>(R.id.clearButton).setOnClickListener { clear() }
        findViewById<Button>(R.id.equalsButton).setOnClickListener {
            buttonShowResult()
        }

        val buttonNegate = findViewById<Button>(R.id.negativeButton)
        buttonNegate.setOnClickListener { negateNumber() }

        val buttonBackSpace = findViewById<Button>(R.id.backSpaceButton)
        buttonBackSpace.setOnClickListener { backspace() }
    }

    private fun prepareOperation(operation: String) {
        error = false
        lastOperation = this.operation
        this.operation = operation
        if (lastOperation == "") {
            lastOperation = operation
        }
        displayOperationTextView.text=this.operation
        // example like 2+2+2
        if (!calculated) {
            val number = displayTextView.text.toString()
            if (firstNumber.isEmpty()) {
                firstNumber = number
            } else {
                secondNumber = number
                calculate(lastOperation)
                repeat = true
            }
        }
        calculated = false
        displayTextView.text = ""
    }

    private fun calculate(op: String) {
        if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && op.isNotEmpty()) {
            var res: Double = firstNumber.toDouble()
            when (op) {
                "+" -> res += secondNumber.toDouble()

                "-" -> res -= secondNumber.toDouble()

                "*" -> res *= secondNumber.toDouble()

                "/" -> {
                    if (secondNumber == "0") {
                        Log.d("calc", "Error")
                        displayTextView.text = "Error"
                        error = true
                    } else {
                        res /= secondNumber.toDouble()
                    }
                }
            }
            if (!displayTextView.text.equals("Error") && secondNumber != "" && !error) {
                Log.d("calc", "$firstNumber $op $secondNumber")
                firstNumber = res.toString()
                Log.d("result", "=$firstNumber")
            }
        }
    }

    private fun showResult() {
        if (displayTextView.text != "Error" && !error) {
            if (firstNumber == "") {
                firstNumber = "0.0"
            }
            displayTextView.text = firstNumber
            Log.d("result", "$firstNumber ")
        }
    }

    private fun buttonShowResult() {
        if ((displayTextView.text != "" && secondNumber == "") || !calculated) {
            secondNumber = displayTextView.text.toString()
            repeat = false
        }
        Log.d("resCalc", "$firstNumber $operation $secondNumber")
        calculate(operation)
        showResult()
        calculated = true
    }

    private fun clear() {
        if (cleared) {
            displayTextView.text = ""
            displayTextSave = ""
            firstNumber = ""
            secondNumber = ""
            operation = ""
            lastOperation = ""
            calculated = false
            repeat = false
            cleared = false
            error = false
            Log.d("clear2", "empty")
        } else {
            secondNumber = ""
            displayTextView.text = ""
            cleared = true
            error = false
            Log.d("clear1", "empty textView, $firstNumber, $operation $secondNumber")
        }
    }

    private fun negateNumber() {
        val currentText = displayTextView.text.toString()
        // Check if the string is not empty and is a valid number
        if (currentText.isNotEmpty() && currentText.toDoubleOrNull() != null) {
            val number = currentText.toDouble()
            val negatedNumber = -number
            displayTextView.text = negatedNumber.toString()
            if (calculated) {
                firstNumber = negatedNumber.toString()
            }
        }
    }

    private fun appendNumber(number: String) {
        if (!(number != "0" && displayTextView.text.toString() == "0")) {
            displayTextView.append(number)
            cleared = false
        }
    }

    private fun backspace() {
        val text = displayTextView.text
        displayTextView.text = text.substring(0, text.length - 1)
    }

    private fun appendDecimalPoint() {
        if (!displayTextView.text.contains(".")) {
            if (displayTextView.text.isEmpty()) {
                displayTextView.append("0.")
            } else {
                displayTextView.append(".")
            }
        }
    }
}

