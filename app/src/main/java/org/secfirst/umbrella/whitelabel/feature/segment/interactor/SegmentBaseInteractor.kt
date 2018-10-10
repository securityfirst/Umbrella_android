package org.secfirst.umbrella.whitelabel.feature.segment.interactor

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface SegmentBaseInteractor : BaseInteractor {

    suspend fun fetchSubcategoryBy(subcategoryId: Long): Subject?

    suspend fun fetchCategoryBy(categoryId: Long): Module?

    suspend fun insertChecklist(checklist: Checklist)

    suspend fun insertMarkdown(markdown: Markdown)
}