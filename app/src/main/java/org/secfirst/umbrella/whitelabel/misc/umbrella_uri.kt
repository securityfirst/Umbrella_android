package org.secfirst.umbrella.whitelabel.misc

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.coroutines.withContext
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module_Table
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject_Table
import org.secfirst.umbrella.whitelabel.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyController
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.HostFormController
import org.secfirst.umbrella.whitelabel.feature.reader.view.HostReaderController
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.HostSegmentController
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext

const val FORM_HOST = "forms"
const val FEED_HOST = "feed"
const val CHECKLIST_HOST = "checklist"
const val SEARCH_HOST = "search"
/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/glossary{1}
 Open a specific segment for category (glossary)
 */
const val LESSON_MODULE_LEVEL = 1
/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/information{1}/beginner{2}/email{3}
 Open a specific difficulty for subject (email)
 */
const val LESSON_SUBJECT_LEVEL = 3

/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/glossary{1}/android-apk{2}
 Open a specific segment for category (android-apk)
 umbrella://lesson/tools/tor
 */
const val LESSON_SEGMENT_IN_MODULE = 2

/*
 Scheme to define how many levels a URI should have.
 Eg: umbrella://lesson/information{1}/beginner{2}/email{3}/what_now{4}
 Open a specific segment for subject (email)
 */
const val LESSON_SEGMENT_IN_SUBJECT = 4
const val SCHEMA = "umbrella://"

suspend fun isLessonDeepLink(pathSplitted: List<String>): Boolean {
    var res = false
    if (pathSplitted.last().contains("s_", true))
        res = true
    else {
        if (pathSplitted.size == 3) {
            val subject = getSubjectByRootDir(pathSplitted[1])
            val difficulty = getDifficultyBySubjectId(subject?.id ?: "")
            difficulty.forEach { diff ->
                if (diff.rootDir == pathSplitted.last())
                    res = true
            }
        }
    }
    return res
}

fun String.deepLinkIdentifier() = this.removePrefix("s_")
        .removePrefix("f_")
        .removePrefix("c_")
        .substringBeforeLast(".md")
        .substringBeforeLast(".yml")
        .removeSpecialCharacter().toLowerCase()

private fun getUriSplitted(path: String) = path.split("/")

fun openFeedByUrl(router: Router, navigation: BottomNavigationView) {
    router.pushController(RouterTransaction.with(HostReaderController()))
    navigation.menu.getItem(0).isChecked = true
}

fun openFormByUrl(router: Router, navigation: BottomNavigationView, path: String) {
    router.pushController(RouterTransaction.with(HostFormController(path)))
    navigation.menu.getItem(1).isChecked = true
}

fun openChecklistByUrl(router: Router, navigation: BottomNavigationView, uriString: String) {
    router.pushController(RouterTransaction.with(HostChecklistController(uriString)))
    navigation.menu.getItem(2).isChecked = true
}

fun openSpecificLessonByUrl(router: Router, navigation: BottomNavigationView, uriString: String) {
    router.pushController(RouterTransaction.with(HostSegmentController(uriString)))
    navigation.menu.getItem(3).isChecked = true
}

fun openDifficultyByUrl(router: Router, navigation: BottomNavigationView, path: String) {
    val moduleName = getUriSplitted(path)[0]
    val subjectName = getUriSplitted(path).last()
    launchSilent(uiContext) {
        val module = getModuleBy(moduleName)
        module?.subjects?.forEach { subject ->
            if (subject.rootDir == subjectName.decapitalize())
                router.pushController(RouterTransaction.with(DifficultyController(subject.id, true)))

            navigation.menu.getItem(3).isChecked = true
        }
    }
}

private suspend fun getModuleBy(rootDir: String) =
        withContext(ioContext) {
            SQLite.select()
                    .from(Module::class.java)
                    .where(Module_Table.rootDir.`is`(rootDir))
                    .querySingle()
        }

suspend fun getDifficultyBySubjectId(subjectId: String): List<Difficulty> = withContext(ioContext) {
    SQLite.select()
            .from(Difficulty::class.java)
            .where(Difficulty_Table.subject_id.`is`(subjectId))
            .queryList()
}

suspend fun getSubjectByRootDir(rootDir: String): Subject? = withContext(ioContext) {
    SQLite.select()
            .from(Subject::class.java)
            .where(Subject_Table.rootDir.`is`(rootDir))
            .querySingle()
}
