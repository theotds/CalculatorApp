package com.newcalculator.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.newcalculator.R

class SimpleCalculatorFragment : Fragment() {

    private lateinit var displayTextView: TextView
    private var displayTextSave: String = ""
    private var firstNumber: String = ""
    private var secondNumber: String = ""
    private var operation: String = ""
    private var lastOperation: String = ""
    private var calculated: Boolean = false
    private var repeat: Boolean = false
    private var cleared: Boolean = false
    private var error: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_calculator, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(outState!=null){
            displayTextSave=displayTextView.text.toString()
            outState.putString("displayTextSave", displayTextSave)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSavedData(savedInstanceState,view)
        simpleCalculator(view)
    }

    private fun getSavedData(savedInstanceState: Bundle?, view: View) {
        if (savedInstanceState != null) {
            savedInstanceState.let {
                displayTextSave = it.getString("displayTextSave", "")
                firstNumber = it.getString("firstNumber", "")
                secondNumber = it.getString("secondNumber", "")
                operation = it.getString("operation", "")
                lastOperation = it.getString("lastOperation", "")
                calculated = it.getBoolean("calculated", false)
                repeat = it.getBoolean("repeat", false)
                cleared = it.getBoolean("cleared", false)
                error = it.getBoolean("error", false)
            }
            displayTextView = view.findViewById(R.id.displayTextView)
            displayTextView.text = displayTextSave
        }
    }

    private fun simpleCalculator(view: View) {
        displayTextView = view.findViewById(R.id.displayTextView)

        // IDs for number buttons
        val numberButtonsIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )

        val decimalButton = view.findViewById<Button>(R.id.decimalButton)
        decimalButton.setOnClickListener { appendDecimalPoint() }

        val operationButtonsIdsToOperation = mapOf(
            R.id.addButton to "+",
            R.id.subtractButton to "-",
            R.id.multiplyButton to "*",
            R.id.divideButton to "/",
        )

        numberButtonsIds.forEach { id ->
            view.findViewById<Button>(id)
                .setOnClickListener { appendNumber((it as Button).text.toString()) }
        }

        operationButtonsIdsToOperation.forEach { (id, operation) ->
            view.findViewById<Button>(id).setOnClickListener {
                prepareOperation(operation)
            }
        }

        view.findViewById<Button>(R.id.clearButton).setOnClickListener { clear() }
        view.findViewById<Button>(R.id.equalsButton).setOnClickListener {
            buttonShowResult()
        }

        val buttonNegate = view.findViewById<Button>(R.id.negativeButton)
        buttonNegate.setOnClickListener { negateNumber() }

        val buttonBackSpace = view.findViewById<Button>(R.id.backSpaceButton)
        buttonBackSpace.setOnClickListener { backspace() }
    }

    private fun prepareOperation(operation: String) {
        error=false
        lastOperation = this.operation
        this.operation = operation
        if (lastOperation == "") {
            lastOperation = operation
        }
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
                        error=true
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
            repeat=false
        }
        Log.d("resCalc", "$firstNumber $operation $secondNumber")
        calculate(operation)
        showResult()
        calculated = true
    }

    private fun clear() {
        if (cleared) {
            displayTextView.text=""
            displayTextSave=""
            firstNumber= ""
            secondNumber = ""
            operation = ""
            lastOperation = ""
            calculated= false
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

