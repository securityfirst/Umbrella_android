package org.secfirst.umbrella.whitelabel.feature.lesson.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.lesson_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.about.AboutController
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyController
import org.secfirst.umbrella.whitelabel.feature.lesson.DaggerLessonComponent
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.presenter.LessonBasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.view.HostSegmentController
import javax.inject.Inject

class LessonController : BaseController(), LessonView {

    @Inject
    internal lateinit var presenter: LessonBasePresenter<LessonView, LessonBaseInteractor>
    private val lessonClick: (Subject) -> Unit = this::onLessonClicked
    private val groupClick: (String) -> Unit = this::onGroupClicked
    private lateinit var lessonAdapter: LessonAdapter

    override fun onInject() {
        DaggerLessonComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    private fun onLessonClicked(subject: Subject) {
        presenter.submitSelectLesson(subject)
    }

    private fun onGroupClicked(moduleSha1ID: String) {
        presenter.submitSelectHead(moduleSha1ID)
    }

    override fun onAttach(view: View) {
        lessonMenu?.layoutManager = LinearLayoutManager(context)
        presenter.onAttach(this)
        presenter.submitLoadAllLesson()
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.lesson_view, container, false)
    }


    override fun showAllLesson(lessons: List<Lesson>) {
        lessonAdapter = LessonAdapter(lessons, lessonClick, groupClick)
        lessonMenu?.adapter = lessonAdapter
    }

    override fun startDifficultyController(subject: Subject) {
        router.pushController(RouterTransaction.with(DifficultyController(subject)))
    }

    override fun startDeferredSegment(selectDifficulty: Difficulty) {
        router.pushController(RouterTransaction.with(HostSegmentController(selectDifficulty)))
    }

    override fun startSegmentController(module: Module) {
        router.pushController(RouterTransaction.with(HostSegmentController(module)))
    }

    override fun startSegmentController(subject: Subject) {
        router.pushController(RouterTransaction.with(HostSegmentController(subject)))

    }

    override fun startSegmentDetail(markdown: Markdown) {
        router.pushController(RouterTransaction.with(AboutController(markdown)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lessonAdapter.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        lessonAdapter.onSaveInstanceState(savedInstanceState)
    }

    private fun setUpToolbar() {
        lessonToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.lesson_title)
        }
    }
}