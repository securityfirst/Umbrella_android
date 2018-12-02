package org.secfirst.umbrella.whitelabel.data.disk

import javax.inject.Inject

class TentRepository @Inject constructor(private val tentDao: TentDao) : TentRepo {


    override suspend fun updateRepository() = tentDao.rebaseBranch()

    override suspend fun loadElementsFile() = tentDao.filterCategoryFiles()

    override fun loadFile() = tentDao.filterByElement()

    override suspend fun fetchRepository() = tentDao.cloneRepository()
}