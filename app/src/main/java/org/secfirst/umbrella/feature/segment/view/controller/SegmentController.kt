package org.secfirst.umbrella.feature.segment.view.controller

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.commonsware.cwac.anddown.AndDown
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.empty_view.view.*
import kotlinx.android.synthetic.main.segment_view.view.*
import kotlinx.android.synthetic.main.share_dialog.view.*
import org.apache.commons.io.FilenameUtils.removeExtension
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.secfirst.umbrella.R
import org.secfirst.umbrella.BuildConfig.APPLICATION_ID
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.InfiniteScrollListener
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.covertToHTML
import org.secfirst.umbrella.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.feature.segment.SegmentPagination
import org.secfirst.umbrella.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.feature.segment.view.SegmentFoot
import org.secfirst.umbrella.feature.segment.view.SegmentItem
import org.secfirst.umbrella.feature.segment.view.SegmentView
import org.secfirst.umbrella.feature.segment.view.adapter.GroupAdapter
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.createDocument
import org.secfirst.umbrella.misc.launchSilent
import java.io.File
import javax.inject.Inject


class SegmentController(bundle: Bundle) : BaseController(bundle), SegmentView {

    @Inject
    internal lateinit var presenter: SegmentBasePresenter<SegmentView, SegmentBaseInteractor>
    private val segmentClick: (Int) -> Unit = this::onSegmentClicked
    private val checklistFavoriteClick: (Boolean) -> Unit = this::onChecklistFavoriteClick
    private val segmentFavoriteClick: (Markdown) -> Unit = this::onSegmentFavoriteClick
    private val checklistShareClick: () -> Unit = this::onChecklistShareClick
    private val segmentShareClick: (Markdown) -> Unit = this::onSegmentShareClick
    private val footClick: (Int) -> Unit = this::onFootClicked
    private val markdownIds by lazy { args.getStringArrayList(EXTRA_SEGMENT) }
    private val checklistId by lazy { args.getString(EXTRA_CHECKLIST) }
    private val isFromDashboard by lazy { args.getBoolean(EXTRA_DASHBOARD) }
    private var checklist: Checklist? = null
    private lateinit var shareDialog: AlertDialog
    private lateinit var shareView: View
    private val segmentAdapter = GroupAdapter()
    private lateinit var markdownPagination: SegmentPagination
    private lateinit var viewSegment: View
    private lateinit var tabControl: HostSegmentTabControl

    constructor(
        markdownIds: ArrayList<String>,
        checklistId: String,
        isFromDashboard: Boolean = false
    ) : this(Bundle().apply {
        putStringArrayList(EXTRA_SEGMENT, markdownIds)
        putString(EXTRA_CHECKLIST, checklistId)
        putBoolean(EXTRA_DASHBOARD, isFromDashboard)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
            .application(UmbrellaApplication.instance)
            .build()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        tabControl = parentController as HostSegmentTabControl
        viewSegment = inflater.inflate(R.layout.segment_view, container, false)
        shareView = inflater.inflate(R.layout.share_dialog, container, false)
        if (markdownIds?.isNotEmpty() == true) {
            shareDialog = AlertDialog
                .Builder(activity)
                .setView(shareView)
                .create()
            presenter.onAttach(this)
            checklistId?.let {
                markdownIds?.let { it1 ->
                    presenter.submitMarkdownsAndChecklist(it1, it)
                }
            }
            if (isFromDashboard) {
                onSegmentClicked(markdownIds!!.size)
            }
        } else {
            viewSegment.segmentEmptyView.visibility = View.VISIBLE
            viewSegment.emptyTitleView.text = context.getString(R.string.empty_favorites_message)
        }
        return viewSegment
    }

    private fun initSegmentRecycler(view: View) {

        val flexboxLayoutManager = FlexboxLayoutManager(applicationContext).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.FLEX_START
        }

        view.segmentRecyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter = segmentAdapter
            addOnScrollListener(object : InfiniteScrollListener(LinearLayoutManager(context)) {
                override fun onLoadMore(currentPage: Int) {
                    createSegmentCards(markdownPagination.nextPage())
                }
            })
        }
    }

    override fun showSegments(markdowns: List<Markdown>, checklist: Checklist?) {
        this.checklist = checklist
        initSegmentRecycler(viewSegment)
        initSegmentView(markdowns)
    }

    private fun initSegmentView(markdowns: List<Markdown>) {
        markdownPagination = SegmentPagination(markdowns.toMutableList())
        createSegmentCards(markdownPagination.nextPage())
        createChecklistCard()
    }

    private fun createSegmentCards(markdowns: List<Markdown>) {
        val section = Section()
        launchSilent(uiContext) {
            markdowns.forEach { markdown ->
                if (markdown.isRemove) {
                    val segmentItem =
                        SegmentItem(segmentClick, segmentShareClick, segmentFavoriteClick, null)
                    section.add(segmentItem)
                } else {
                    val segmentItem =
                        SegmentItem(segmentClick, segmentShareClick, segmentFavoriteClick, markdown)
                    section.add(segmentItem)
                }
            }

            if (segmentAdapter.getGroupSize() > 0 && checklist != null)
                segmentAdapter.add(segmentAdapter.lastGroupPosition(), section)
            else
                segmentAdapter.add(section)
        }
    }

    private fun createChecklistCard() {
        checklist?.let {
            segmentAdapter.add(
                Section(
                    SegmentFoot(
                        footClick,
                        checklistShareClick, checklistFavoriteClick, it
                    )
                )
            )
        }
    }

    override fun showSegmentDetail(markdown: Markdown) {
        parentController?.router?.pushController(
            RouterTransaction.with(
                SegmentDetailController(
                    markdown
                )
            )
        )

    }

    private fun onChecklistFavoriteClick(isFavorite: Boolean) {
        checklist?.favorite = isFavorite
        if (checklist != null)
            presenter.submitChecklistFavorite(checklist!!)
    }


    private fun onChecklistShareClick() {
        val checklistHtml = checklist?.covertToHTML()
        val doc = Jsoup.parse(checklistHtml)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        showShareDialog(doc, context.getString(R.string.checklistDetail_title))
    }

    private fun onSegmentShareClick(markdown: Markdown) {
        val andDown = AndDown()
        val result = andDown.markdownToHtml(markdown.text, AndDown.HOEDOWN_EXT_QUOTE, 0)
        val doc = Jsoup.parse(result)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        showShareDialog(doc, markdown.title)
    }

    fun getTitle(): String = "Lesson"

    private fun shareDocument(fileToShare: File) {
        val pm = context.packageManager
        val uri = FileProvider.getUriForFile(context, APPLICATION_ID, fileToShare)
        val shareIntent = activity?.let {
            ShareCompat.IntentBuilder(it)
                .setType(context.contentResolver.getType(uri))
                .setStream(uri)
                .intent
        }

        shareIntent?.action = Intent.ACTION_SEND
        shareIntent?.putExtra(Intent.EXTRA_SUBJECT, removeExtension(fileToShare.name))
        shareIntent?.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareIntent?.resolveActivity(pm) != null)
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.export_lesson)
                )
            )

    }

    private fun showShareDialog(doc: Document, title: String) {
        var type = context.getString(R.string.pdf_name)
        shareView.pdfRadio.text = context.getString(R.string.pdf_name)
        shareView.htmlRadio.text = context.getString(R.string.html_name)

        shareView.shareDocumentButton.setOnClickListener {
            shareDocument(createDocument(doc, title, type, context))
            shareDialog.dismiss()
        }
        shareView.cancelShareButton.setOnClickListener { shareDialog.dismiss() }
        shareView.shareGroup.setOnCheckedChangeListener { _, checkedId ->
            type = if (shareView.pdfRadio.id == checkedId)
                context.getString(R.string.pdf_name)
            else
                context.getString(R.string.html_name)
        }
        shareDialog.show()
    }

    private fun onSegmentFavoriteClick(markdown: Markdown) =
        presenter.submitMarkdownFavorite(markdown)

    private fun onFootClicked(position: Int) {
        markdownIds?.let {
            tabControl.moveTabAt(it.size + 1)
        }
    }

    private fun onSegmentClicked(position: Int) = tabControl.moveTabAt(position + 1)

    companion object {
        const val EXTRA_SEGMENT = "selected_segment"
        const val EXTRA_CHECKLIST = "selected_checklist"
        const val EXTRA_DASHBOARD = "from_dashboard"
    }
}
