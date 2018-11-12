package org.secfirst.umbrella.whitelabel.feature.reader.view.server

import Extensions
import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bluelinelabs.conductor.RouterTransaction
import com.raizlabs.android.dbflow.config.FlowManager
import kotlinx.android.synthetic.main.server_view.*
import org.apache.commons.io.FileUtils
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.disk.TentConfig
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourController


class ServerController : BaseController(), View.OnClickListener {


    override fun onInject() {
    }

    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.server_view, container, false)
        val button = view.findViewById<Button>(R.id.sharedb)
        button.setOnClickListener { showFileChooserPreview() }
        val okButton = view.findViewById<Button>(R.id.okserver)
        okButton.setOnClickListener(this)
        return view
    }

    override fun onAttach(view: View) {

    }

    override fun onClick(v: View) {
        v.isEnabled = false
        changeUrlServer()
    }

    private fun changeUrlServer() {
        TentConfig.uriRepository = editServer?.text.toString()
        FileUtils.deleteQuietly(context.cacheDir)
        FlowManager.getDatabase(AppDatabase.NAME).reset()
        router.pushController(RouterTransaction.with(TourController()))
    }

    private fun showFileChooserPreview() {
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            shareDbFile(AppDatabase.NAME)
        } else {
            requestExternalStoragePermission()
        }

    }

    private fun requestExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Request the permission
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_EXTERNAL_STORAGE)
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_EXTERNAL_STORAGE)
        }
    }

    private fun shareDbFile(fileName: String) {
        Extensions.copyFile(activity!!)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                showFileChooserPreview()
            } else {
                // Permission request was denied.
            }
        }
    }

}