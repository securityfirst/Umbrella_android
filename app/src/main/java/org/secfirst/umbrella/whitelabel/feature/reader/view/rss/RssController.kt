package org.secfirst.umbrella.whitelabel.feature.reader.view.rss

import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.add_rss_dialog.view.*
import kotlinx.android.synthetic.main.rss_view.*
import kotlinx.android.synthetic.main.rss_view.view.*
import org.jetbrains.anko.design.longSnackbar
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.SwipeToDeleteCallback
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject


class RssController : BaseController(), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var rssAdapter: RssAdapter
    private lateinit var rssDialogView: View
    private lateinit var alertDialog: AlertDialog
    private var stateRecycler: Parcelable? = null
    private val onClick: (RSS) -> Unit = this::onClickOpenArticle

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.rss_view, container, false)
        rssDialogView = inflater.inflate(R.layout.add_rss_dialog, container, false)
        alertDialog = AlertDialog
                .Builder(activity)
                .setView(rssDialogView)
                .create()
        presenter.onAttach(this)
        initDeleteChecklistItem(view)
        rssAdapter = RssAdapter(onClick)
        view.rssRecycleView.initRecyclerView(rssAdapter)
        rssAdapter.removeAll()
        presenter.submitFetchRss()

        rssDialogView.rssOk.setOnClickListener { addRss() }
        rssDialogView.rssCancel.setOnClickListener { alertDialog.dismiss() }
        view.addRss.setOnClickListener { alertDialog.show() }
        return view
    }

    private fun onClickOpenArticle(rss: RSS) {
        parentController?.router?.pushController(RouterTransaction.with(ArticleController(rss)))
    }

    private fun addRss() {
        presenter.submitInsertRss(RSS(rssDialogView.rssEditText.text.toString()))
        alertDialog.dismiss()
    }

    override fun showRss(rss: RSS) {
        rssAdapter.add(rss)
    }

    override fun showRssError() {
        val snackBar = rssView?.longSnackbar(context.resources.getString(R.string.rss_message_error))
        val snackView = snackBar?.view
        snackView?.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    }

    override fun showNewestRss(rss: RSS) = rssAdapter.add(rss)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        stateRecycler = rssRecycleView?.layoutManager?.onSaveInstanceState()
        outState.putParcelable(EXTRA_RECYCLER_STATE, stateRecycler)
    }

    override fun onRestoreInstanceState(saveInstanceState: Bundle) {
        super.onRestoreInstanceState(saveInstanceState)
        stateRecycler = saveInstanceState.getParcelable(EXTRA_RECYCLER_STATE)
    }

    private fun initDeleteChecklistItem(view: View) {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val rssSelected = rssAdapter.getAt(position)
                presenter.submitDeleteRss(rssSelected)
                rssAdapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(view.rssRecycleView)
    }

    companion object {
        private const val EXTRA_RECYCLER_STATE = "extra_recycler_state"
    }
}