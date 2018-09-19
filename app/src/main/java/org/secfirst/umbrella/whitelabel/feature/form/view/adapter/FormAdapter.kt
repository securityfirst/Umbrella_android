package org.secfirst.umbrella.whitelabel.feature.form.view.adapter

import android.support.annotation.IntRange
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import org.jetbrains.anko.AnkoContext
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.Form
import org.secfirst.umbrella.whitelabel.feature.form.view.FormUI
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.FormController

class FormAdapter(private val form: Form, private val controller: FormController, private val listOfViews: MutableList<FormUI>)
    : AbstractStepAdapter(UmbrellaApplication.instance) {

    private val pages = SparseArray<Step>()

    override fun getViewModel(@IntRange(from = 0) position: Int): StepViewModel = StepViewModel.Builder(context)
            .setTitle(form.screens[position].title)
            .create()

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        var step: Step? = pages.get(position)
        if (step == null) {
            step = createStep(position)
            pages.put(position, step)
        }
        val stepView = step as FormUI
        val view = stepView.createView(AnkoContext.create(context, controller, false))
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)

    override fun findStep(position: Int): Step? = if (pages.size() > 0) pages[position] else null

    override fun isViewFromObject(view: View, `object`: Any) = view === `object`

    override fun createStep(position: Int) = listOfViews[position]

    override fun getCount() = form.screens.size
}