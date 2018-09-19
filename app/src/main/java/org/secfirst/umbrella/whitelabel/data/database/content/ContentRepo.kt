package org.secfirst.umbrella.whitelabel.data.database.content

import org.secfirst.umbrella.whitelabel.data.Root


interface ContentRepo {

    suspend fun insertAllLessons(root: Root)

}