package org.secfirst.umbrella.feature.form.view.controller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.form_host_view.*
import org.secfirst.umbrella.BuildConfig
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.data.database.form.ActiveForm
import org.secfirst.umbrella.data.database.form.Form
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.form.DaggerFormComponent
import org.secfirst.umbrella.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.feature.form.presenter.FormBasePresenter
import org.secfirst.umbrella.feature.form.view.FormView
import org.secfirst.umbrella.feature.form.view.adapter.ActiveFormSection
import org.secfirst.umbrella.feature.form.view.adapter.AllFormSection
import org.secfirst.umbrella.misc.currentTime
import java.io.File
import javax.inject.Inject


class HostFormController(bundle: Bundle) : BaseController(bundle), FormView {

    @Inject
    internal lateinit var presenter: FormBasePresenter<FormView, FormBaseInteractor>
    private val editClick: (Form) -> Unit = this::onEditFormClicked
    private val editActiveFormClick: (ActiveForm) -> Unit = this::onEditActiveFormClicked
    private val deleteClick: (Int, ActiveForm) -> Unit = this::onDeleteFormClicked
    private val shareClick: (ActiveForm) -> Unit = this::onShareFormClicked
    private val sectionAdapter by lazy { SectionedRecyclerViewAdapter() }
    private var allFormTag = ""
    private var activeFormTag = ""
    private lateinit var allFormSection: AllFormSection
    private lateinit var activeFormSection: ActiveFormSection
    private val uriString by lazy { args.getString(EXTRA_ENABLE_DEEP_LINK_FORM)?:"" }


    constructor(uri: String = "") : this(Bundle().apply {
        putString(EXTRA_ENABLE_DEEP_LINK_FORM, uri)
    })

    override fun onInject() {
        DaggerFormComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        hostFormRecycleView.layoutManager = LinearLayoutManager(view.context)
        presenter.onAttach(this)
        checkFormURI()
    }

    private fun checkFormURI() {
        if (uriString.isBlank())
            presenter.submitLoadAllForms()
        else
            presenter.submitFormByURI(uriString)
    }

    private fun onEditFormClicked(form: Form) {
        val activeForm = ActiveForm()
        activeForm.form = form
        activeForm.title = form.title
        activeForm.date = currentTime
        activeForm.sha1Form = form.path
        router.pushController(RouterTransaction.with(FormController(activeForm)))
    }

    private fun onEditActiveFormClicked(activeForm: ActiveForm) {
        router.pushController(RouterTransaction.with(FormController(activeForm)))
    }

    private fun onDeleteFormClicked(position: Int, activeForm: ActiveForm) {
        activeFormSection.remove(position, sectionAdapter)
        presenter.submitDeleteActiveForm(activeForm)
    }

    private fun onShareFormClicked(activeForm: ActiveForm) {
        presenter.submitShareFormHtml(activeForm)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        return inflater.inflate(R.layout.form_host_view, container, false)
    }

    override fun showModelAndActiveForms(modelForms: MutableList<Form>, activeForms: MutableList<ActiveForm>) {
        sectionAdapter.removeAllSections()
        sectionAdapter.notifyDataSetChanged()

        resources?.let {
            allFormTag = it.getString(R.string.message_title_all_forms)
            activeFormTag = it.getString(R.string.message_title_active_forms)
        }

        hostFormRecycleView?.let {
            allFormSection = AllFormSection(editClick, allFormTag, modelForms)
            activeFormSection = ActiveFormSection(editActiveFormClick, deleteClick, shareClick, activeFormTag, activeForms)
            sectionAdapter.addSection(allFormSection)
            sectionAdapter.addSection(activeFormSection)
            it.adapter = sectionAdapter
        }
    }

    override fun showShareForm(shareFile: File) {
        activity?.let {
            val uri = FileProvider.getUriForFile(it, BuildConfig.APPLICATION_ID, shareFile)
            val shareIntent = ShareCompat.IntentBuilder.from(it)
                    .setType(it.contentResolver.getType(uri))
                    .setStream(uri)
                    .intent

            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            if (shareIntent.resolveActivity(it.packageManager) != null)
                ContextCompat.startActivity(it, Intent.createChooser(shareIntent, ""), null)
        }
    }

    override fun openSpecificForm(form: Form) = onEditFormClicked(form)

    private fun setUpToolbar() {
        hostFormToolbar?.let {
//            mainActivity.setSupportActionBar(it)
            it.title = context.getString(R.string.form_title)
        }
    }

    companion object {
        private const val EXTRA_ENABLE_DEEP_LINK_FORM = "deeplink"
    }
}
