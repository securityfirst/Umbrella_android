package org.secfirst.umbrella.whitelabel.feature.account

import Extensions
import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bluelinelabs.conductor.Controller
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase

class AccountController : Controller() {

    private val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.account_view, container, false)
        val button = view.findViewById<Button>(R.id.sharedb)
        button.setOnClickListener { showFileChooserPreview() }
        return view
    }

    override fun onAttach(view: View) {

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