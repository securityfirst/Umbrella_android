package org.secfirst.umbrella.whitelabel.feature.checklist.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface ChecklistBaseInteractor : BaseInteractor {

    suspend fun fetchChecklistBy(id: Long): Checklist?

}