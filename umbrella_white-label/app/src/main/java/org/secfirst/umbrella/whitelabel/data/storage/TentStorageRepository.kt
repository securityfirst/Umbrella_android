package org.secfirst.umbrella.whitelabel.data.storage

import java.io.File
import javax.inject.Inject

class TentStorageRepository @Inject constructor(private val tentStorageDao: TentStorageDao,
                                                private val tentConfig: TentConfig) : TentStorageRepo {

    override suspend fun getElementsFile(): List<File> = tentStorageDao.filterBySubElement(tentConfig)

    override suspend fun getLoadersFile(): List<File> = tentStorageDao.filterByElement(tentConfig)

    override suspend fun fetch() = tentStorageDao.cloneRepository(tentConfig)
}