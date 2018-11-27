package org.secfirst.umbrella.whitelabel.data.disk

import javax.inject.Inject

class TentRepository @Inject constructor(private val tentDao: TentDao,
                                         private val tentConfig: TentConfig) : TentRepo {


    override suspend fun updateRepository() = tentDao.rebaseBranch(tentConfig)

    override fun loadCategoryImage(imgName: String) = tentDao.filterImageCategoryFile(imgName, tentConfig)

    override suspend fun loadElementsFile() = tentDao.filterCategoryFiles(tentConfig)

    override fun loadFile() = tentDao.filterByElement(tentConfig)

    override suspend fun fetchRepository() = tentDao.cloneRepository(tentConfig)
}