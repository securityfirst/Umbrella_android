package org.secfirst.umbrella.whitelabel.feature.tour.view

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.tour_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.disk.baseUrlRepository
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.ACTION_COMPLETED_FOREGROUND_SERVICE
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_ID
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_PROGRESS
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_TITLE_PROGRESS
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity
import org.secfirst.umbrella.whitelabel.feature.tour.DaggerTourComponent
import javax.inject.Inject


class TourController : BaseController(), ContentView {

    @Inject
    internal lateinit var presenter: ContentBasePresenter<ContentView, ContentBaseInteractor>
    private var viewList: MutableList<TourUI> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog

    override fun onInject() {
        DaggerTourComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    init {
        viewList.add(TourUI(R.color.umbrella_purple_dark, R.drawable.umbrella190, org.secfirst.umbrella.whitelabel.R.string.tour_slide_1_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.umbrella_green, R.drawable.walktrough2, org.secfirst.umbrella.whitelabel.R.string.tour_slide_2_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.umbrella_yellow, R.drawable.walktrough3, org.secfirst.umbrella.whitelabel.R.string.tour_slide_3_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.umbrella_purple, R.drawable.walktrough4, org.secfirst.umbrella.whitelabel.R.string.terms_conditions, GONE, VISIBLE))
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val percentage = intent.getIntExtra(EXTRA_CONTENT_SERVICE_PROGRESS, -1)
            val title = intent.getStringExtra(EXTRA_CONTENT_SERVICE_TITLE_PROGRESS) ?: ""
            val isCompleted = intent.getBooleanExtra(ACTION_COMPLETED_FOREGROUND_SERVICE, false)

            if (title.isNotEmpty()) {
                progressDialog.setTitle(title)
            }
            Handler().post {
                progressDialog.progress = 0
                progressDialog.incrementProgressBy(percentage)
            }

            if (isCompleted) {
                progressDialog.dismiss()
                router.popCurrentController()
                startActivity(Intent(context, MainActivity::class.java))
                startActivity(Intent())
                mainActivity.navigationPositionToCenter()
                enableNavigation(true)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.tour_view, container, false)
        progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.setProgressStyle(R.style.ProgressDialogStyle)
        progressDialog.progress = 0
        progressDialog.setTitle(context.getString(R.string.notification_fetching_data))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        return view
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        enableNavigation(false)
        initViewPager()
        onAcceptButton()
        presenter.onAttach(this)
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mMessageReceiver, IntentFilter(EXTRA_CONTENT_SERVICE_ID))
    }


    private fun initViewPager() {
        tourViewPager?.let {
            val tourAdapter = TourAdapter(this)
            it.adapter = tourAdapter
            tourAdapter.setData(viewList)
            pageIndicatorView.count = tourAdapter.count
            it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    pageSelected(position)
                }
            })
        }
    }

    private fun pageSelected(position: Int) {
        pageIndicatorView.selection = position
        if (position == viewList.lastIndex)
            acceptButton?.let { btn -> btn.visibility = VISIBLE }
        else
            acceptButton?.let { btn -> btn.visibility = INVISIBLE }
    }

    override fun downloadContentCompleted(res: Boolean) {
        progressDialog.dismiss()
        if (res) {
            router.pushController(RouterTransaction.with(HostChecklistController()))
            mainActivity.navigationPositionToCenter()
            enableNavigation(true)
        } else
            view?.let {
                Snackbar.make(it,
                        it.resources.getString(org.secfirst.umbrella.whitelabel.R.string.error_connection_tour_message), Snackbar.LENGTH_LONG).show()
            }
    }

    override fun downloadContentInProgress() = progressDialog.show()

    private fun onAcceptButton() {
        acceptButton?.setOnClickListener { presenter.manageContent(baseUrlRepository) }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(mMessageReceiver)
    }
}