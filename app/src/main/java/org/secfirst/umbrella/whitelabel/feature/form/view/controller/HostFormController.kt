package org.secfirst.umbrella.whitelabel.feature.form.view.controller

import android.content.Intent
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.host_form_view.*
import org.secfirst.umbrella.whitelabel.BuildConfig
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.ActiveForm
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.form.DaggerFormComponent
import org.secfirst.umbrella.whitelabel.feature.form.interactor.FormBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.form.presenter.FormBasePresenter
import org.secfirst.umbrella.whitelabel.feature.form.view.FormView
import org.secfirst.umbrella.whitelabel.feature.form.view.adapter.ActiveFormSection
import org.secfirst.umbrella.whitelabel.feature.form.view.adapter.AllFormSection
import org.secfirst.umbrella.whitelabel.misc.currentTime
import java.io.File
import javax.inject.Inject


class HostFormController : BaseController(), FormView {

    @Inject
    internal lateinit var presenter: FormBasePresenter<FormView, FormBaseInteractor>
    private val editClick: (Form) -> Unit = this::onEditFormClicked
    private val editActiveFormClick: (ActiveForm) -> Unit = this::onEditActiveFormClicked
    private val deleteClick: (Int, ActiveForm) -> Unit = this::onDeleteFormClicked
    private val shareClick: (ActiveForm) -> Unit = this::onShareFormClicked
    private val sectionAdapter: SectionedRecyclerViewAdapter by lazy { SectionedRecyclerViewAdapter() }
    private var allFormTag = ""
    private var activeFormTag = ""
    private lateinit var allFormSection: AllFormSection
    private lateinit var activeFormSection: ActiveFormSection

    override fun onInject() {
        DaggerFormComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        enableArrowBack(false)
        presenter.onAttach(this)
        allFormRecycleView.layoutManager = LinearLayoutManager(view.context)
        presenter.submitLoadAllForms()
    }

    private fun onEditFormClicked(form: Form) {
        val activeForm = ActiveForm()
        activeForm.form = form
        activeForm.id = System.currentTimeMillis()
        activeForm.title = form.title
        activeForm.date = currentTime
        activeForm.referenceId = form.id
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.host_form_view, container, false)
    }

    override fun showModelAndActiveForms(modelForms: MutableList<Form>, activeForms: MutableList<ActiveForm>) {
        sectionAdapter.removeAllSections()
        sectionAdapter.notifyDataSetChanged()

        resources?.let {
            allFormTag = it.getString(R.string.message_title_all_forms)
            activeFormTag = it.getString(R.string.message_title_active_forms)
        }

        allFormRecycleView?.let {

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

    override fun getEnableBackAction() = true

    override fun getToolbarTitle() = context.getString(R.string.form_title)


}
