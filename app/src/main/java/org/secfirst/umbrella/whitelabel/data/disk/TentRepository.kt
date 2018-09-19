package org.secfirst.umbrella.whitelabel.data.disk

import java.io.File
import javax.inject.Inject

class TentRepository @Inject constructor(private val tentDao: TentDao,
                                         private val tentConfig: TentConfig) : TentRepo {

    override fun loadCategoryImage(imgName : String) = tentDao.filterByCategoryImage(imgName,tentConfig)

    override suspend fun loadElementsFile(): List<File> = tentDao.filterBySubElement(tentConfig)

    override fun loadFile(): List<File> = tentDao.filterByElement(tentConfig)

    override suspend fun fetch() = tentDao.cloneRepository(tentConfig)
}