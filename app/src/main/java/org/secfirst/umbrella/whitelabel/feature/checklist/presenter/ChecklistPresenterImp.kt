package org.secfirst.umbrella.whitelabel.feature.checklist.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import org.secfirst.umbrella.whitelabel.misc.runBlockingSilent
import javax.inject.Inject

class ChecklistPresenterImp<V : ChecklistView, I :
ChecklistBaseInteractor> @Inject constructor(interactor: I) :
        BasePresenterImp<V, I>(interactor = interactor), ChecklistBasePresenter<V, I> {


    override fun submitInsertCustomChecklist(checklistTitle: String, idChecklist: String,
                                             checklistValue: List<String>) {
        runBlockingSilent(uiContext) {
            interactor?.let {
                val contents = mutableListOf<Content>()
                val customChecklist = Checklist(contents, true, checklistTitle, idChecklist)
                checklistValue.forEach { value ->
                    val content = Content(value)
                    content.checklist = customChecklist
                    contents.add(content)
                }
                it.persistChecklist(customChecklist)
            }
        }
    }

    override fun submitDeleteChecklistContent(checklistContent: Content) {
        runBlockingSilent(uiContext) {
            interactor?.deleteChecklistContent(checklistContent)
        }
    }

    override fun submitDisableChecklistContent(checklistContent: Content) {
        runBlockingSilent(uiContext) {
            interactor?.disableChecklistContent(checklistContent)
        }
    }

    override fun submitLoadCustomDashboard() {
        runBlockingSilent(uiContext) {
            interactor?.let {
                val customChecklist = it.fetchAllCustomChecklistInProgress()
                val allDashboard = mutableListOf<Dashboard.Item>()
                val inProgressList = customDashboard(customChecklist, "My checklists")
                allDashboard.addAll(inProgressList)
                getView()?.showDashboard(allDashboard)
            }
        }
    }

    override fun submitLoadDashboard() {
        runBlockingSilent(uiContext) {
            interactor?.let {
                val rate = it.fetchAllChecklistInProgress()
                val totalDone = it.fetchChecklistCount().toInt()
                val allDashboard = mutableListOf<Dashboard.Item>()
                allDashboard.addAll(totalDoneDashboard(rate.size, totalDone))
                val favoriteList = dashboardMount(it.fetchAllChecklistFavorite(), "Favorites")
                allDashboard.addAll(favoriteList)
                val inProgressList = dashboardMount(it.fetchAllChecklistInProgress(), "My Checklists")
                allDashboard.addAll(inProgressList)
                getView()?.showDashboard(allDashboard)
            }
        }
    }

    private fun customDashboard(customChecklist: List<Checklist>, title: String): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val dashboardTitle = Dashboard.Item(title)
        dashboards.add(dashboardTitle)
        customChecklist.forEach {
            val dashboardItem = Dashboard.Item(it.progress, it.title, it, null)
            dashboards.add(dashboardItem)
        }
        return dashboards
    }

    private suspend fun dashboardMount(itemList: List<Checklist>, title: String): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val dashboardTitle = Dashboard.Item(title)
        dashboards.add(dashboardTitle)
        interactor?.let { interactor ->
            itemList.forEach { checklist ->
                val difficultyId = checklist.difficulty?.id
                if (difficultyId != null) {
                    val loadDifficulty = interactor.fetchDifficultyById(difficultyId)
                    val subject = interactor.fetchSubjectById(loadDifficulty.subject!!.id)
                    val dashboardItem = Dashboard.Item(checklist.progress, subject!!.title, checklist, loadDifficulty)
                    dashboards.add(dashboardItem)
                }
            }
        }
        return dashboards
    }

    private fun totalDoneDashboard(rate: Int, totalDone: Int): List<Dashboard.Item> {
        val dashboards = mutableListOf<Dashboard.Item>()
        val percentage = rate * 100.0 / totalDone
        val dashboardItem = Dashboard.Item(percentage.toInt(), "Total Done", null, null)
        val dashboardTitle = Dashboard.Item("Checklist Total")
        dashboards.add(dashboardTitle)
        dashboards.add(dashboardItem)
        return dashboards
    }

    override fun submitInsertChecklistContent(checklistContent: Content) {
        runBlockingSilent(uiContext) {
            interactor?.persistChecklistContent(checklistContent)
        }
    }

    override fun submitUpdateChecklist(checklist: Checklist) {
        runBlockingSilent(uiContext) {
            interactor?.persistChecklist(checklist)
        }
    }
}