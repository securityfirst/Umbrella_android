package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

interface SegmentRepo {
    suspend fun loadSubcategoryBy(categoryId: Long): Subcategory
}