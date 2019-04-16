package org.secfirst.umbrella.whitelabel.feature.maskapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.calculator_view.*
import kotlinx.android.synthetic.main.calculator_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import org.secfirst.umbrella.whitelabel.feature.maskapp.DaggerMaskAppComponent
import org.secfirst.umbrella.whitelabel.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.maskapp.presenter.MaskAppBasePresenter
import org.secfirst.umbrella.whitelabel.feature.maskapp.setMaskModeDelayed
import java.text.DecimalFormat
import javax.inject.Inject


class CalculatorController : BaseController(), MaskAppView, SensorEventListener {

    @Inject
    internal lateinit var presenter: MaskAppBasePresenter<MaskAppView, MaskAppBaseInteractor>
    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0

    companion object {
        private const val ADDITION = '+'
        private const val SUBTRACTION = '-'
        private const val MULTIPLICATION = '*'
        private const val DIVISION = '/'
        const val EXTRA_CALCULATOR_VIEW = "calculator_action"
        private var CURRENT_ACTION: Char = ' '
        private var valueOne = java.lang.Double.NaN
        private var valueTwo: Double = 0.toDouble()
        private var decimalFormat: DecimalFormat = DecimalFormat("#.##########")
    }

    override fun onInject() {
        DaggerMaskAppComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.calculator_view, container, false)
        init(view)
        presenter.onAttach(this)
        return view
    }

    @SuppressLint("SetTextI18n")
    private fun init(view: View) {

        view.buttonDot.setOnClickListener { editTextCalc.setText("${editTextCalc.text}.") }
        view.buttonZero.setOnClickListener { editTextCalc.setText("${editTextCalc.text}0") }
        view.buttonOne.setOnClickListener { editTextCalc.setText("${editTextCalc.text}1") }
        view.buttonTwo.setOnClickListener { editTextCalc.setText("${editTextCalc.text}2") }
        view.buttonThree.setOnClickListener { editTextCalc.setText("${editTextCalc.text}3") }
        view.buttonFour.setOnClickListener { editTextCalc.setText("${editTextCalc.text}4") }
        view.buttonFive.setOnClickListener { editTextCalc.setText("${editTextCalc.text}5") }
        view.buttonSix.setOnClickListener { editTextCalc.setText("${editTextCalc.text}6") }
        view.buttonSeven.setOnClickListener { editTextCalc.setText("${editTextCalc.text}7") }
        view.buttonEight.setOnClickListener { editTextCalc.setText("${editTextCalc.text}8") }
        view.buttonNine.setOnClickListener { editTextCalc.setText("${editTextCalc.text}9") }

        view.buttonAdd.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = ADDITION
            infoTextView.text = "${decimalFormat.format(valueOne)}+"
            editTextCalc.text = null
        }

        view.buttonSubtract.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = SUBTRACTION
            infoTextView.text = "${decimalFormat.format(valueOne)}-"
            editTextCalc.text = null
        }

        view.buttonMultiply.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = MULTIPLICATION
            infoTextView.text = "${decimalFormat.format(valueOne)}*"
            editTextCalc.text = null
        }

        view.buttonDivide.setOnClickListener {
            computeCalculation()
            CURRENT_ACTION = DIVISION
            infoTextView.text = "${decimalFormat.format(valueOne)}/"
            editTextCalc.text = null
        }

        view.buttonEqual.setOnClickListener {
            computeCalculation()
            val formatValueOne = decimalFormat.format(valueOne)
            val formatValueTwo = decimalFormat.format(valueTwo)
            infoTextView.text = "${infoTextView.text}$formatValueTwo = $formatValueOne"
            valueOne = Double.NaN
            CURRENT_ACTION = '0'
        }

        view.buttonClear.setOnClickListener {
            if (editTextCalc.text.isNotEmpty()) {
                val currentText = editTextCalc.text
                editTextCalc.setText(currentText.subSequence(0, currentText.length - 1))
            } else {
                valueOne = Double.NaN
                valueTwo = Double.NaN
                editTextCalc.setText("")
                infoTextView.text = ""
            }
        }
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()
    }

    private fun openActivity() {
        activity?.let { safeActivity ->
            val intent = Intent(safeActivity, MainActivity::class.java)
            intent.putExtra(EXTRA_CALCULATOR_VIEW, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            safeActivity.finish()
            context.setMaskModeDelayed(9000)
        }
    }

    override fun onAttach(view: View) {
        enableNavigation(false)
        super.onAttach(view)
    }

    override fun handleBack(): Boolean {
        mainActivity.finish()
        return super.handleBack()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event)
        }
    }

    private fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        // Movement
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH)
        val actualTime = event.timestamp
        if (accelationSquareRoot >= 2)
        //
        {
            if (actualTime - lastUpdate < 1000) {
                return
            }
            lastUpdate = actualTime
            openActivity()
        }
    }

    private fun computeCalculation() {
        if (!java.lang.Double.isNaN(valueOne)) {
            valueTwo = editTextCalc.text.toString().toDouble()
            editTextCalc.text = null
            when (CURRENT_ACTION) {
                ADDITION -> valueOne += valueTwo
                SUBTRACTION -> valueOne -= valueTwo
                MULTIPLICATION -> valueOne *= valueTwo
                DIVISION -> valueOne /= valueTwo
            }
        } else {
            valueOne = editTextCalc.text.toString().toDouble()
        }
    }
}