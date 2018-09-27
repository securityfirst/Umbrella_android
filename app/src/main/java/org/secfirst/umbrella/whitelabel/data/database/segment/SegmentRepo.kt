package org.secfirst.umbrella.whitelabel.data.database.segment

import org.secfirst.umbrella.whitelabel.data.database.content.Category
import org.secfirst.umbrella.whitelabel.data.database.content.Subcategory

interface SegmentRepo {
    suspend fun loadSubcategoryBy(subcategoryId: Long): Subcategory?

    suspend fun loadCategoryBy(categoryId: Long): Category?
}