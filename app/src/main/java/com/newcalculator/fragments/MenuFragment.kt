package com.newcalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.newcalculator.R
import kotlin.system.exitProcess

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.simpleCalculatorButton).setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_simpleCalculatorFragment)
        }

        view.findViewById<Button>(R.id.advancedCalculatorButton).setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_advancedCalculatorFragment)
        }

        view.findViewById<Button>(R.id.about).setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_aboutFragment)
        }

        val exitButton = view.findViewById<Button>(R.id.exit)
        exitButton.setOnClickListener { exitProcess(0) }
    }
}
