package org.secfirst.umbrella.whitelabel.data.database.content

import org.secfirst.umbrella.whitelabel.data.disk.Root


interface ContentRepo {

    suspend fun insertAllLessons(root: Root)

}