package com.newcalculator.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.newcalculator.R
import kotlin.math.*

class AdvancedCalculator : AppCompatActivity() {

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
    private var operationClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.advanced_calculator)

        getSavedData(savedInstanceState)
        simpleCalculator()
        advancedCalculator()
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
            outState.putBoolean("operationClicked", operationClicked)
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
                operationClicked = it.getBoolean("operationClicked", false)
            }
            displayTextView = findViewById(R.id.displayTextView)
            displayTextView.text = displayTextSave

            displayOperationTextView = findViewById(R.id.displayTextViewOperation)
            displayOperationTextView.text = displayOperationTextViewSave
        }
    }

    private fun advancedCalculator() {
        val sinButton: Button = findViewById(R.id.sinButton)
        val cosButton: Button = findViewById(R.id.cosButton)
        val tanButton: Button = findViewById(R.id.tanButton)
        val percentageButton: Button = findViewById(R.id.percentButton)
        val sqrtButton: Button = findViewById(R.id.sqrtButton)
        val powerButton: Button = findViewById(R.id.powButton)
        val powerYButton: Button = findViewById(R.id.powYButton)
        val logButton: Button = findViewById(R.id.logButton)
        val lnButton: Button = findViewById(R.id.lnButton)

        sinButton.setOnClickListener { calculateSin() }
        cosButton.setOnClickListener { calculateCos() }
        tanButton.setOnClickListener { calculateTan() }
        percentageButton.setOnClickListener { calculatePercentage() }
        sqrtButton.setOnClickListener { calculateSqrt() }
        logButton.setOnClickListener { calculateLog() }
        lnButton.setOnClickListener { calculateLn() }
        powerYButton.setOnClickListener { prepareOperation("pow") }
        powerButton.setOnClickListener { calculatePow() }

    }

    private fun calculatePow() {
        firstNumber = displayTextView.text.toString().toDouble().pow(2).toString()
        showResult()
    }

    private fun simpleCalculator() {
        displayTextView = findViewById(R.id.displayTextView)
        displayOperationTextView = findViewById(R.id.displayTextViewOperation)

        // IDs for number buttons
        val numberButtonsIds = listOf(
            R.id.button0,
            R.id.button1,
            R.id.button2,
            R.id.button3,
            R.id.button4,
            R.id.button5,
            R.id.button6,
            R.id.button7,
            R.id.button8,
            R.id.button9
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
            findViewById<Button>(id).setOnClickListener { appendNumber((it as Button).text.toString()) }
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
        lastOperation = this.operation
        error = false
        this.operation = operation
        if (lastOperation == "") {
            lastOperation = operation
        }
        displayOperationTextView.text = "operation: " + this.operation
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
        } else if (firstNumber.isEmpty()) {
            val number = displayTextView.text.toString()
            firstNumber = number
        }
        calculated = false
        operationClicked = true
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
                        val text = "Can't divide by 0!"
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(this, text, duration) // in Activity
                        toast.show()
                        error = true
                        error()
                    } else {
                        res /= secondNumber.toDouble()
                    }
                }

                "pow" -> {
                    res = firstNumber.toDouble().pow(secondNumber.toDouble())
                }
            }
            if (secondNumber != "" && !error) {
                Log.d("calc", "$firstNumber $op $secondNumber")
                firstNumber = res.toString()
                Log.d("result", "=$firstNumber")
            }
        }
    }

    private fun error() {
        fullClear()
        Log.d("errorClear", "empty")
    }

    private fun showResult() {
        if (!error) {
            val number = firstNumber.toDoubleOrNull()
            number?.let {
                displayTextView.text =
                    if (number % 1.0 == 0.0) number.toInt().toString() else number.toString()
            }
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
            fullClear()
            Log.d("clear2", "empty")
        } else {
            secondNumber = ""
            displayTextView.text = ""
            cleared = true
            error = false
            Log.d("clear1", "empty textView, $firstNumber, $operation $secondNumber")
        }
    }

    private fun fullClear() {
        displayTextView.text = ""
        displayTextSave = ""
        firstNumber = ""
        secondNumber = ""
        operation = ""
        lastOperation = ""
        displayOperationTextView.text = "operation:"
        displayOperationTextViewSave = ""
        calculated = false
        repeat = false
        cleared = false
        error = false
        operationClicked = false
    }

    private fun negateNumber() {
        val currentText = displayTextView.text.toString()
        if (currentText.isNotEmpty() && currentText.toDoubleOrNull() != null) {
            val number = currentText.toDouble()
            val negatedNumber = -number

            displayTextView.text = if (number % 1.0 == 0.0) negatedNumber.toInt()
                .toString() else negatedNumber.toString()
            if (calculated) {
                firstNumber = if (number % 1.0 == 0.0) negatedNumber.toInt()
                    .toString() else negatedNumber.toString()
            }
        }
    }

    private fun appendNumber(number: String) {
        if (operationClicked) {
            displayTextView.text = ""
            operationClicked = false
        }
        if (calculated) {
            fullClear()
        }
        if (!(number != "0" && displayTextView.text.toString() == "0")) {
            displayTextView.append(number)
            cleared = false
        }
    }

    private fun backspace() {
        val text = displayTextView.text
        if (text.length > 0) {
            displayTextView.text = text.substring(0, text.length - 1)
        }
    }

    private fun appendDecimalPoint() {
        if (!displayTextView.text.contains(".")) {
            if (operationClicked) {
                displayTextView.text = ""
                operationClicked = false
            }
            if (calculated) {
                fullClear()
            }
            if (displayTextView.text.isEmpty()) {
                displayTextView.append("0.")
            } else {
                displayTextView.append(".")
            }
        }
    }

    private fun calculateSin() {
        val currentText = displayTextView.text.toString()
        currentText.toDoubleOrNull()?.let {
            val result = sin(Math.toRadians(it))
            firstNumber = result.toString()
        }
        showResult()
        calculated=true
    }

    private fun calculateCos() {
        val currentText = displayTextView.text.toString()
        currentText.toDoubleOrNull()?.let {
            val result = cos(Math.toRadians(it))
            firstNumber = result.toString()
        }
        showResult()
        calculated=true
    }

    private fun calculateTan() {
        val currentText = displayTextView.text.toString()
        currentText.toDoubleOrNull()?.let {
            val result = tan(Math.toRadians(it))
            // Check for undefined result which occurs at 90°, 270°, etc.
            if (result.isFinite()) {
                firstNumber = result.toString()
                calculated = true
            } else {

                val text = "tan of 90 is infinite"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
                error = true
                error()
            }
        }
        showResult()
    }

    private fun calculatePercentage() {
        val currentText = displayTextView.text.toString()
        currentText.toDoubleOrNull()?.let {
            val result = it / 100
            displayTextView.text =
                if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
            calculated = true
        }
    }

    private fun calculateSqrt() {
        val currentText = displayTextView.text.toString()
        currentText.toDoubleOrNull()?.let {
            if (it >= 0) {
                val result = sqrt(it)
                displayTextView.text =
                    if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
                calculated = true
            } else {
                val text = "sqrt of a number must be higher or equal 0"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
                error = true
                error()
            }
        }
    }

    private fun calculateLog() {
        displayTextView.text.toString().toDoubleOrNull()?.let {
            if (it > 0) { // Logarithm is only defined for positive numbers
                val result = log10(it)
                displayTextView.text =
                    if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
                calculated = true
            } else {
                val text = "log of a number must be higher than 0"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
                error = true
                error()
            }
        }
    }

    // Natural logarithm calculation
    private fun calculateLn() {
        displayTextView.text.toString().toDoubleOrNull()?.let {
            if (it > 0) { // Natural logarithm is only defined for positive numbers
                val result = ln(it)
                displayTextView.text =
                    if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
                calculated = true
            } else {
                val text = "ln of a number must be higher than 0"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
                error = true
                error()
            }
        }
    }
}

