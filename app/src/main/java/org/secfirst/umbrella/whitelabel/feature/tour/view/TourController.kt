package org.secfirst.umbrella.whitelabel.feature.tour.view

import android.app.Dialog
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.tour_view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.component.DialogManager.Companion.PROGRESS_DIALOG_TAG
import org.secfirst.umbrella.whitelabel.data.disk.baseUrlRepository
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.content.presenter.ContentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.tour.DaggerTourComponent
import javax.inject.Inject


class TourController : BaseController(), ContentView {

    @Inject
    internal lateinit var presenter: ContentBasePresenter<ContentView, ContentBaseInteractor>
    private var viewList: MutableList<TourUI> = mutableListOf()
    private lateinit var dialogManager: DialogManager
    private lateinit var progressDialog: ProgressDialog
    private var notificationManager: NotificationManager? = null


    override fun onInject() {
        DaggerTourComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    init {
        viewList.add(TourUI(org.secfirst.umbrella.whitelabel.R.color.umbrella_purple_dark, org.secfirst.umbrella.whitelabel.R.drawable.umbrella190, org.secfirst.umbrella.whitelabel.R.string.tour_slide_1_text, VISIBLE, GONE))
        viewList.add(TourUI(org.secfirst.umbrella.whitelabel.R.color.umbrella_green, org.secfirst.umbrella.whitelabel.R.drawable.walktrough2, org.secfirst.umbrella.whitelabel.R.string.tour_slide_2_text, VISIBLE, GONE))
        viewList.add(TourUI(org.secfirst.umbrella.whitelabel.R.color.umbrella_yellow, org.secfirst.umbrella.whitelabel.R.drawable.walktrough3, org.secfirst.umbrella.whitelabel.R.string.tour_slide_3_text, VISIBLE, GONE))
        viewList.add(TourUI(org.secfirst.umbrella.whitelabel.R.color.umbrella_purple, org.secfirst.umbrella.whitelabel.R.drawable.walktrough4, org.secfirst.umbrella.whitelabel.R.string.terms_conditions, GONE, VISIBLE))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        dialogManager = DialogManager(this)
        return inflater.inflate(org.secfirst.umbrella.whitelabel.R.layout.tour_view, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        enableNavigation(false)
        initViewPager()
        onAcceptButton()
        presenter.onAttach(this)
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
                    pageIndicatorView.selection = position

                    if (position == viewList.lastIndex)
                        acceptButton?.let { btn -> btn.visibility = VISIBLE }
                    else
                        acceptButton?.let { btn -> btn.visibility = INVISIBLE }
                }
            })
        }
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

    override fun downloadContentInProgress() {
        view?.let {
            doLongOperation()
        }
    }

    private fun onAcceptButton() {
        acceptButton?.let { btn ->
            btn.onClick {
                presenter.manageContent(baseUrlRepository)
            }
        }
    }

    override fun onDownloadSuccess() {
        progressDialog.setMessage(context.getString(org.secfirst.umbrella.whitelabel.R.string.loading_tour_download_message))
    }

    override fun onProcessProgress() {
        progressDialog.setMessage(context.getString(org.secfirst.umbrella.whitelabel.R.string.loading_tour_parse_message))

    }

    override fun onStoredProgress() {
        progressDialog.setMessage(context.getString(org.secfirst.umbrella.whitelabel.R.string.loading_tour_store_message))
    }

    private fun doLongOperation() {
        dialogManager.showDialog(PROGRESS_DIALOG_TAG, object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                progressDialog = ProgressDialog(context)
                progressDialog.setCancelable(false)
                progressDialog.setMessage(context?.getString(org.secfirst.umbrella.whitelabel.R.string.loading_tour_download_message))
                return progressDialog
            }
        })
    }
}