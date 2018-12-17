package org.secfirst.umbrella.whitelabel.feature.maskapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.tbouron.shakedetector.library.ShakeDetector
import kotlinx.android.synthetic.main.calculator_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import java.text.DecimalFormat

class CalculatorView : AppCompatActivity() {

    private val ADDITION = '+'
    private val SUBTRACTION = '-'
    private val MULTIPLICATION = '*'
    private val DIVISION = '/'
    private var CURRENT_ACTION: Char = ' '
    private var valueOne = java.lang.Double.NaN
    private var valueTwo: Double = 0.toDouble()
    private var decimalFormat: DecimalFormat = DecimalFormat("#.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculator_view)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        ShakeDetector.create(this) { startShakeDetector() }
        buttonDot.setOnClickListener { editTextCalc.setText("${editTextCalc.text}.") }
        buttonZero.setOnClickListener { editTextCalc.setText("${editTextCalc.text}0") }
        buttonOne.setOnClickListener { editTextCalc.setText("${editTextCalc.text}1") }
        buttonTwo.setOnClickListener { editTextCalc.setText("${editTextCalc.text}2") }
        buttonThree.setOnClickListener { editTextCalc.setText("${editTextCalc.text}3") }
        buttonFour.setOnClickListener { editTextCalc.setText("${editTextCalc.text}4") }
        buttonFive.setOnClickListener { editTextCalc.setText("${editTextCalc.text}5") }
        buttonSix.setOnClickListener { editTextCalc.setText("${editTextCalc.text}6") }
        buttonSeven.setOnClickListener { editTextCalc.setText("${editTextCalc.text}7") }
        buttonEight.setOnClickListener { editTextCalc.setText("${editTextCalc.text}8") }
        buttonNine.setOnClickListener { editTextCalc.setText("${editTextCalc.text}9") }

        buttonAdd.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = ADDITION
            infoTextView.text = decimalFormat.format(valueOne) + "+"
            editTextCalc.text = null
        }

        buttonSubtract.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = SUBTRACTION
            infoTextView.text = decimalFormat.format(valueOne) + "-"
            editTextCalc.text = null
        }

        buttonMultiply.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = MULTIPLICATION
            infoTextView.text = decimalFormat.format(valueOne) + "*"
            editTextCalc.text = null
        }

        buttonDivide.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = DIVISION
            infoTextView.text = decimalFormat.format(valueOne) + "/"
            editTextCalc.text = null
        }

        buttonEqual.setOnClickListener {
            computeCalculation()
            infoTextView.text = infoTextView.text.toString() +
                    decimalFormat.format(valueTwo) + " = " + decimalFormat.format(valueOne)
            valueOne = java.lang.Double.NaN
            CURRENT_ACTION = '0'
        }

        buttonClear.setOnClickListener {
            if (editTextCalc.text.isNotEmpty()) {
                val currentText = editTextCalc.text
                editTextCalc.setText(currentText.subSequence(0, currentText.length - 1))
            } else {
                valueOne = java.lang.Double.NaN
                valueTwo = java.lang.Double.NaN
                editTextCalc.setText("")
                infoTextView.text = ""
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ShakeDetector.start()
    }

    override fun onStop() {
        super.onStop()
        ShakeDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        ShakeDetector.destroy()
    }

    private fun startShakeDetector() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun computeCalculation() {
        if (!java.lang.Double.isNaN(valueOne)) {
            valueTwo = java.lang.Double.parseDouble(editTextCalc.text.toString())
            editTextCalc.text = null
            when (CURRENT_ACTION) {
                ADDITION -> valueOne += valueTwo
                SUBTRACTION -> valueOne -= valueTwo
                MULTIPLICATION -> valueOne *= valueTwo
                DIVISION -> valueOne /= valueTwo
            }
        } else {
            valueOne = java.lang.Double.parseDouble(editTextCalc.text.toString())
        }
    }
}