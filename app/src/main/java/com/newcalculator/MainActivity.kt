package com.newcalculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.newcalculator.fragments.About
import com.newcalculator.fragments.AdvancedCalculator
import com.newcalculator.fragments.SimpleCalculator
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
    }

    fun setupUI(){
        findViewById<Button>(R.id.simpleCalculatorButton).setOnClickListener {
            val intent = Intent(this, SimpleCalculator::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.advancedCalculatorButton).setOnClickListener {
            val intent = Intent(this, AdvancedCalculator::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.about).setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        val exitButton = findViewById<Button>(R.id.exit)
        exitButton.setOnClickListener { exitProcess(0) }
    }
}
