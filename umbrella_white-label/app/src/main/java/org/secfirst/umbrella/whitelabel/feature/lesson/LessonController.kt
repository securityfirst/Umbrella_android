package org.secfirst.umbrella.whitelabel.feature.lesson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import org.secfirst.umbrella.whitelabel.R

class LessonController: Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.lesson_view, container, false)
    }
}