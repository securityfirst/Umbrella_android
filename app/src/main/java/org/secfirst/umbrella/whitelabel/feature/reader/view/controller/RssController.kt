package org.secfirst.umbrella.whitelabel.feature.reader.view.controller

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.rss_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.adapter.RssAdapter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.shareLink
import javax.inject.Inject


class RssController : BaseController(), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private lateinit var rssAdapter: RssAdapter
    private lateinit var rssDialogView: View
    private lateinit var alertDialog: AlertDialog
    private lateinit var rssCancel: AppCompatTextView
    private lateinit var rssOk: AppCompatTextView
    private lateinit var rssEdit: AppCompatEditText
    private lateinit var currentRss: RSS
    private val onLongClick: (RSS) -> Unit = this::onLongClickRss
    private val onClick: (RSS) -> Unit = this::onClickOpenArticle


    private fun onLongClickRss(rss: RSS) {
        currentRss = rss
        activity?.startActionMode(modeCallBack)
    }


    private fun onClickOpenArticle(rss: RSS) {
        parentController?.router?.pushController(RouterTransaction.with(ArticleController(rss)))
    }

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.onAttach(this)
        presenter.submitFetchRss()
        rssOk.setOnClickListener { addRss() }
        rssCancel.setOnClickListener { alertDialog.dismiss() }
        initRecyclerView()
        onClickRss()
    }

    private fun initRecyclerView() {
        rssRecycleView?.let {
            it.layoutManager = LinearLayoutManager(activity)
            it.adapter = rssAdapter
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        onCreateDialogView(inflater, container)
        rssAdapter = RssAdapter(onClick, onLongClick)
        return inflater.inflate(R.layout.rss_view, container, false)
    }

    private fun onCreateDialogView(inflater: LayoutInflater, container: ViewGroup) {
        rssDialogView = inflater.inflate(R.layout.add_rss_dialog, container, false)
        rssCancel = rssDialogView.findViewById(R.id.rssCancel)
        rssOk = rssDialogView.findViewById(R.id.rssOk)
        rssEdit = rssDialogView.findViewById(R.id.rssEditText)
        alertDialog = AlertDialog
                .Builder(activity)
                .setView(rssDialogView)
                .create()
    }


    private fun onClickRss() {
        val dialogManager = DialogManager(this)
        addRss?.let { floatButton ->
            floatButton.setOnClickListener {
                dialogManager.showDialog("TEST", object : DialogManager.DialogFactory {
                    override fun createDialog(context: Context?): Dialog {
                        return alertDialog
                    }
                })
            }
        }
    }

    private fun addRss() {
        presenter.submitInsertRss(RSS(rssEdit.text.toString()))
        alertDialog.dismiss()
    }

    override fun showAllRss(rss: List<RSS>) {
        rssAdapter.addAll(rss)
        Log.e("test", "size - ${rss.size}")
    }

    override fun showNewestRss(rss: RSS) = rssAdapter.add(rss)

    private val modeCallBack = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = "Actions"
            mode.menuInflater.inflate(R.menu.rss_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_rss_share -> {
                    mode.finish()
                    activity?.shareLink(currentRss.link)
                    true
                }
                R.id.action_rss_delete -> {
                    mode.finish()
                    presenter.submitDeleteRss(currentRss)
                    rssAdapter.remove(currentRss)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mode.finish()
        }
    }

    override fun getEnableBackAction() = false

    override fun getToolbarTitle() = ""

}