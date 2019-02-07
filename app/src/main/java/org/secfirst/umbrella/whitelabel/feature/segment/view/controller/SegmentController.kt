package org.secfirst.umbrella.whitelabel.feature.segment.view.controller

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import com.commonsware.cwac.anddown.AndDown
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.segment_view.view.*
import kotlinx.android.synthetic.main.share_dialog.view.*
import org.apache.commons.io.FilenameUtils.removeExtension
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.secfirst.umbrella.whitelabel.BuildConfig.APPLICATION_ID
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.InfiniteScrollListener
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.covertToHTML
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.DaggerSegmentComponent
import org.secfirst.umbrella.whitelabel.feature.segment.SegmentPagination
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.presenter.SegmentBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentFoot
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentItem
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.feature.segment.view.adapter.GroupAdapter
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.createDocument
import org.secfirst.umbrella.whitelabel.misc.launchSilent
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
    private var checklist: Checklist? = null
    private lateinit var shareDialog: AlertDialog
    private lateinit var shareView: View
    private val segmentAdapter = GroupAdapter()
    private lateinit var markdownPagination: SegmentPagination

    constructor(markdownIds: ArrayList<String>, checklistId: String) : this(Bundle().apply {
        putStringArrayList(EXTRA_SEGMENT, markdownIds)
        putString(EXTRA_CHECKLIST, checklistId)
    })

    override fun onInject() {
        DaggerSegmentComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.segment_view, container, false)
        shareView = inflater.inflate(R.layout.share_dialog, container, false)
        shareDialog = AlertDialog
                .Builder(activity)
                .setView(shareView)
                .create()
        presenter.onAttach(this)
        presenter.submitMarkdownsAndChecklist(markdownIds, checklistId)
        initSegmentRecycler(view)

        return view
    }

    private fun initSegmentRecycler(view: View) {
        segmentAdapter.spanCount = 12
        val gridLayoutManager = GridLayoutManager(context, segmentAdapter.spanCount).apply {
            spanSizeLookup = segmentAdapter.spanSizeLookup
        }
        val itemDecor = DividerItemDecoration(activity, gridLayoutManager.orientation)
        view.segmentRecyclerView.apply {
            layoutManager = gridLayoutManager
            removeItemDecoration(itemDecor)
            adapter = segmentAdapter
            addOnScrollListener(object : InfiniteScrollListener(gridLayoutManager) {
                override fun onLoadMore(currentPage: Int) {
                    createSegmentCards(markdownPagination.nextPage())
                }
            })
        }
    }

    override fun showSegments(markdowns: List<Markdown>, checklist: Checklist?) {
        this.checklist = checklist
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
                val segmentItem = SegmentItem(segmentClick, segmentShareClick, segmentFavoriteClick, markdown)
                section.add(segmentItem)
            }
            if (segmentAdapter.getGroupSize() > 0 && checklist != null)
                segmentAdapter.add(segmentAdapter.lastGroupPosition(), section)
            else
                segmentAdapter.add(section)
        }
    }

    private fun createChecklistCard() {
        checklist?.let {
            segmentAdapter.add(Section(SegmentFoot(footClick,
                    checklistShareClick, checklistFavoriteClick, it)))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.search_menu, menu)
    }

    override fun showSegmentDetail(markdown: Markdown) {
        parentController?.router?.pushController(RouterTransaction.with(SegmentDetailController(markdown)))

    }

    private fun onChecklistFavoriteClick(isFavorite: Boolean) {
        checklist?.favorite = isFavorite
        if (checklist != null)
            presenter.submitChecklistFavorite(checklist!!)
    }


    private fun onChecklistShareClick() {
        val checklistHtml = checklist?.covertToHTML()
        val intent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:?subject=Checklist&body=${Uri.encode(checklistHtml)}"))
        startActivity(intent)

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
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setType(context.contentResolver.getType(uri))
                .setStream(uri)
                .intent

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, removeExtension(fileToShare.name))
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareIntent.resolveActivity(pm) != null)
            startActivity(Intent.createChooser(shareIntent, R.string.export_lesson.toString()))

    }

    private fun showShareDialog(doc: Document, title: String) {
        var type = ""
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

    private fun onSegmentFavoriteClick(markdown: Markdown) = presenter.submitMarkdownFavorite(markdown)

    private fun onFootClicked(position: Int) {
        val tabControl = parentController as HostSegmentTabControl
        tabControl.moveTabAt(position + 1)
    }

    private fun onSegmentClicked(position: Int) {
        val test = parentController as HostSegmentTabControl
        test.moveTabAt(position + 1)
    }

    companion object {
        const val EXTRA_SEGMENT = "selected_segment"
        const val EXTRA_CHECKLIST = "selected_checklist"
    }
}
