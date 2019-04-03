package org.secfirst.umbrella.whitelabel.feature.tour.view


import android.app.AlertDialog
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
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.tour_alert.view.*
import kotlinx.android.synthetic.main.tour_view.*
import kotlinx.android.synthetic.main.tour_view.view.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.disk.baseUrlRepository
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.content.ContentService
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.ACTION_COMPLETED_FOREGROUND_SERVICE
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_ID
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_PROGRESS
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_CONTENT_SERVICE_TITLE_PROGRESS
import org.secfirst.umbrella.whitelabel.feature.content.ContentService.Companion.EXTRA_URL_REPOSITORY
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.tour.DaggerTourComponent
import org.secfirst.umbrella.whitelabel.misc.isInternetConnected
import javax.inject.Inject


class TourController : BaseController(), ContentView {

    @Inject
    internal lateinit var presenter: ContentBasePresenter<ContentView, ContentBaseInteractor>
    private var viewList: MutableList<TourUI> = mutableListOf()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var intentService: Intent
    private lateinit var warnerDialog: AlertDialog


    override fun onInject() {
        DaggerTourComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    init {
        viewList.add(TourUI(R.color.umbrella_green, R.drawable.ic_walkthrough1, org.secfirst.umbrella.whitelabel.R.string.tour_slide_1_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.umbrella_yellow, R.drawable.ic_walkthrough2, org.secfirst.umbrella.whitelabel.R.string.tour_slide_2_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.umbrella_purple, R.drawable.ic_walkthrough3, org.secfirst.umbrella.whitelabel.R.string.tour_slide_3_text, VISIBLE, GONE))
        viewList.add(TourUI(R.color.macaroni_and_cheese, R.drawable.walktrough4, org.secfirst.umbrella.whitelabel.R.string.terms_conditions, GONE, VISIBLE))
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val percentage = intent.getIntExtra(EXTRA_CONTENT_SERVICE_PROGRESS, -1)
            val title = intent.getStringExtra(EXTRA_CONTENT_SERVICE_TITLE_PROGRESS) ?: ""
            val isCompleted = intent.getBooleanExtra(ACTION_COMPLETED_FOREGROUND_SERVICE, false)
            val lostConnection = intent.getBooleanExtra(ContentService.ACTION_LOST_CONNECTION, false)

            if (title.isNotEmpty())
                progressDialog.setTitle(title)

            Handler().post {
                progressDialog.progress = 0
                progressDialog.incrementProgressBy(percentage)
            }

            if (isCompleted)
                downloadCompleted()

            if (lostConnection)
                errorLostConnectionMessage()
        }
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        mainActivity.hideNavigation()
        mainActivity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mMessageReceiver, IntentFilter(EXTRA_CONTENT_SERVICE_ID))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.tour_view, container, false)
        val warnerView = inflater.inflate(R.layout.tour_alert, container, false)

        presenter.onAttach(this)
        intentService = Intent(context, ContentService::class.java)
        warnerDialog = AlertDialog
                .Builder(activity)
                .setView(warnerView)
                .create()

        warnerView.ok.onClick { startContentService() }
        warnerView.cancel.onClick { warnerDialog.dismiss() }
        view.acceptButton.setOnClickListener { warnerDialog.show() }
        initProgress()
        enableNavigation(false)
        initViewPager(view)
        return view
    }

    private fun downloadCompleted() {
        progressDialog.dismiss()
        router.popController(this@TourController)
        router.setRoot(RouterTransaction.with(HostChecklistController()))
        mainActivity.navigationPositionToCenter()
        enableNavigation(true)
        mainActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun pageSelected(position: Int) {
        pageIndicatorView.selection = position
        if (position == viewList.lastIndex)
            acceptButton?.let { btn -> btn.visibility = VISIBLE }
        else
            acceptButton?.let { btn -> btn.visibility = INVISIBLE }
    }

    private fun startContentService() {
        warnerDialog.dismiss()
        intentService.apply {
            putExtra(EXTRA_URL_REPOSITORY, baseUrlRepository)
            action = ContentService.ACTION_START_FOREGROUND_SERVICE
        }
        if (context.isInternetConnected()) {
            context.startService(intentService)
            progressDialog.show()
        } else
            tourView?.apply { errorNoConnectionMessage(this) }
    }

    private fun errorNoConnectionMessage(view: View) {
        val snackBar = view.longSnackbar(context.resources.getString(R.string.error_connection_tour_message))
        val snackView = snackBar.view
        snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun errorLostConnectionMessage() {
        tourView?.let {
            val snackBar = it.longSnackbar(context.resources.getString(R.string.notification_lost_connection))
            val snackView = snackBar.view
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        progressDialog.dismiss()
    }

    private fun initProgress() {
        progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.setProgressStyle(R.style.ProgressDialogStyle)
        progressDialog.progress = 0
        progressDialog.setTitle(context.getString(R.string.notification_fetching_data))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    }

    private fun initViewPager(view: View) {
        view.tourViewPager.apply {
            val tourAdapter = TourAdapter(this@TourController)
            adapter = tourAdapter
            tourAdapter.setData(viewList)
            view.pageIndicatorView.count = tourAdapter.count
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    pageSelected(position)
                }
            })
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(mMessageReceiver)
        mainActivity.stopService(intentService)
        progressDialog.dismiss()
    }

    override fun onDestroyView(view: View) {
        mainActivity.stopService(intentService)
        super.onDestroyView(view)
    }
}
