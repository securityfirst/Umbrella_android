package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.content.Subject

interface SegmentRepo {
    suspend fun loadSubcategoryBy(subcategoryId: Long): Subject?

    suspend fun loadCategoryBy(categoryId: Long): Module?
}