package org.secfirst.umbrella.whitelabel.data.storage

import java.io.File


interface   TentStorageRepo {

    suspend fun fetch(): Boolean

    suspend fun getElementsFile(): List<File>

    suspend fun getLoadersFile(): List<File>

}