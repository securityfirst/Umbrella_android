package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.host_checklist.*
import kotlinx.android.synthetic.main.host_reader_view.view.*
import org.jetbrains.anko.toast
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.HostChecklistAdapter
import org.secfirst.umbrella.whitelabel.feature.maskapp.*
import org.secfirst.umbrella.whitelabel.feature.maskapp.view.CalculatorController
import org.secfirst.umbrella.whitelabel.misc.setMaskAppIcon
import javax.inject.Inject


class HostChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView, SensorEventListener {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val uriString by lazy { args.getString(EXTRA_ENABLE_DEEP_LINK_CHECKLIST) ?: "" }
    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0

    constructor(uri: String = "") : this(Bundle().apply {
        putString(EXTRA_ENABLE_DEEP_LINK_CHECKLIST, uri)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        hostChecklistPager?.adapter = HostChecklistAdapter(this)
        hostChecklistTab?.setupWithViewPager(hostChecklistPager)
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onActivityStopped(activity: Activity) {
        sensorManager.unregisterListener(this)
        super.onActivityStopped(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(org.secfirst.umbrella.whitelabel.R.layout.host_checklist, container, false)
        presenter.onAttach(this)
        if (uriString.isNotBlank()) presenter.submitChecklistById(uriString)
        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(org.secfirst.umbrella.whitelabel.R.string.checklist_title)
        }
        mainActivity.navigationPositionToCenter()
        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        lastUpdate = System.currentTimeMillis()
        return view
    }

    override fun onDestroyView(view: View) {
        hostChecklistPager?.adapter = null
        hostChecklistTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }

    companion object {
        private const val EXTRA_ENABLE_DEEP_LINK_CHECKLIST = "deeplink"
    }

    override fun getChecklist(checklist: Checklist) {
        router.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && context.isNotShakeDevice()) {
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
            if (actualTime - lastUpdate < 200) {
                return
            }
            lastUpdate = actualTime
            shakeDevice()
        }
    }

    private fun shakeDevice() {
        if (context.isMaskMode()) {
            setMaskAppIcon(mainActivity, false)
            context.setMaskApp(false)
            context.setShakeDeviceDelayed(3000)
            context.toast(context.getString(org.secfirst.umbrella.whitelabel.R.string.disable_mask_app))
        } else if (context.isNotMaskMode() && context.isNotShakeDevice()) {
            setMaskAppIcon(mainActivity, true)
            context.setMaskApp(true)
            router.pushController(RouterTransaction.with(CalculatorController()))
        }
    }
}
