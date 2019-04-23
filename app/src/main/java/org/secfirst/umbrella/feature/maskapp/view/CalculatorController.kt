package org.secfirst.umbrella.feature.maskapp.view

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.tbouron.shakedetector.library.ShakeDetector
import kotlinx.android.synthetic.main.calculator_view.*
import kotlinx.android.synthetic.main.calculator_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper.Companion.PREF_NAME
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.main.MainActivity
import org.secfirst.umbrella.feature.maskapp.DaggerMaskAppComponent
import org.secfirst.umbrella.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.feature.maskapp.presenter.MaskAppBasePresenter
import org.secfirst.umbrella.misc.setMaskMode
import java.text.DecimalFormat
import javax.inject.Inject

class CalculatorController : BaseController(), MaskAppView {

    @Inject
    internal lateinit var presenter: MaskAppBasePresenter<MaskAppView, MaskAppBaseInteractor>

    companion object {
        private const val ADDITION = '+'
        private const val SUBTRACTION = '-'
        private const val MULTIPLICATION = '*'
        private const val DIVISION = '/'
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
        enableNavigation(false)
        mainActivity.hideNavigation()
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
    }

    override fun onAttach(view: View) {
        ShakeDetector.create(context) { startShakeDetector() }
        ShakeDetector.start()
        mainActivity.hideNavigation()
        super.onAttach(view)
    }

    override fun onDestroy() {
        ShakeDetector.destroy()
        super.onDestroy()
    }

    private fun setShowMockView() {
        val shared = mainActivity.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        shared.edit().putBoolean(AppPreferenceHelper.EXTRA_SHOW_MOCK_VIEW, false).apply()
    }

    private fun startShakeDetector() {
        activity?.let { safeActivity ->
            setMaskMode(safeActivity, true)
            setShowMockView()
            val intent = Intent(safeActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            safeActivity.finish()
        }
    }

    override fun handleBack(): Boolean {
        mainActivity.finish()
        return super.handleBack()
    }

    override fun isMaskApp(res: Boolean) {
        activity?.let { safeActivity ->
            val intent = Intent(safeActivity, MainActivity::class.java)
            if (res) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                safeActivity.finish()
            }
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