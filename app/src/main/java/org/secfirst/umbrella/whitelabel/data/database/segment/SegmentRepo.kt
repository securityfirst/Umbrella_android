package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject

interface SegmentRepo {
    suspend fun loadSubject(subjectId: Long): Subject?

    suspend fun loadModule(moduleId: Long): Module?

    suspend fun loadMarkdowns(subjectId: Long): List<Markdown>

    suspend fun saveChecklist(checklist: Checklist)

    suspend fun saveMarkdown(markdown: Markdown)

}