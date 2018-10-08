package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class ChecklistPresenterImp<V : ChecklistView, I :
ChecklistBaseInteractor> @Inject constructor(interactor: I) :
        BasePresenterImp<V, I>(interactor = interactor), ChecklistBasePresenter<V, I> {

    override fun submitChecklistProgressDone() {
        launchSilent(uiContext) {
            interactor?.let {
                val checklistTotalDone = it.fetchChecklistProgressDone()
                val totalDone = it.fetchChecklistCount().toInt()
                val allDashboard = mutableListOf<Dashboard.Item>()
                allDashboard.addAll(totalDoneDashboard(checklistTotalDone.size, totalDone))
                val favoriteList = dashBoardMount(it.fetchAllChecklistFavorite(), "Favorites")
                allDashboard.addAll(favoriteList)
                val inProgressList = dashBoardMount(it.fetchAllChecklistInProgress(), "My Checklists")
                allDashboard.addAll(inProgressList)
                getView()?.showDashboard(allDashboard)
            }
        }
    }

    private suspend fun dashBoardMount(checklist: List<Checklist>, title: String): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val dashboardTitle = Dashboard.Item(title)
        dashboards.add(dashboardTitle)
        interactor?.let { interactor ->
            checklist.forEach {
                val difficultyId = it.difficulty?.id
                if (difficultyId != null) {
                    val loadDifficulty = interactor.fetchDifficultyById(difficultyId)
                    val subject = interactor.fetchSubjectById(loadDifficulty.subject!!.id)
                    val dashboardItem = Dashboard.Item(it.progress, subject!!.title)
                    dashboards.add(dashboardItem)
                }
            }
        }
        return dashboards
    }

    private fun totalDoneDashboard(rate: Int, totalDone: Int): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val percentage = rate * 100.0 / totalDone
        val dashboardItem = Dashboard.Item(percentage.toInt(), "Total Done")
        val dashboardTitle = Dashboard.Item("Checklist Total")
        dashboards.add(dashboardTitle)
        dashboards.add(dashboardItem)
        return dashboards
    }

    override fun submitInsertChecklistContent(checklistContent: Content) {
        launchSilent(uiContext) {
            interactor?.persistChecklistContent(checklistContent)
        }
    }

    override fun submitUpdateChecklist(checklist: Checklist) {
        launchSilent(uiContext) {
            interactor?.persistChecklist(checklist)
        }
    }
}