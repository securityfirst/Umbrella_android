package org.secfirst.umbrella.whitelabel.feature.lesson.view.controller

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.lesson_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.lesson.DaggerLessonComponent
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.presenter.LessonBasePresenter
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter.LessonMenuAdapter
import javax.inject.Inject

class LessonMenuController : BaseController(), LessonView {

    @Inject
    internal lateinit var presenter: LessonBasePresenter<LessonView, LessonBaseInteractor>
    private val lessonClick: (Lesson.Topic) -> Unit = this::onLessonClicked
    private lateinit var lessonAdapter: LessonMenuAdapter

    override fun onInject() {
        DaggerLessonComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    private fun onLessonClicked(topic: Lesson.Topic) {
//        presenter.submitLessonSelect(topic)
    }


    override fun onAttach(view: View) {
        lessonMenu?.layoutManager = LinearLayoutManager(context)
        presenter.onAttach(this)
        presenter.submitLoadAllLesson()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.lesson_view, container, false)
    }


    override fun showAllLesson(lessons: List<Lesson>) {
        lessonAdapter = LessonMenuAdapter(lessons, lessonClick)
        lessonMenu?.adapter = lessonAdapter
    }

    override fun showSelectDifficult(difficults: List<Difficult>) {
        router.pushController(RouterTransaction.with(DifficultController(difficults)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lessonAdapter.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lessonAdapter.onSaveInstanceState(savedInstanceState)
    }

    override fun getEnableBackAction() = true

    override fun getToolbarTitle(): String = context.getString(R.string.lesson_title)

}