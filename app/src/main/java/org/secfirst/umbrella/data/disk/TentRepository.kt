package org.secfirst.umbrella.data.disk

import java.io.File
import javax.inject.Inject

class TentRepository @Inject constructor(private val tentDao: TentDao) : TentRepo {


    override fun loadFormFile(path: String): List<File> = tentDao.filterForms(path)

    override suspend fun updateRepository() = tentDao.rebaseBranch()

    override suspend fun loadElementsFile(path: String) = tentDao.filterCategories(path)

    override suspend fun fetchRepository(url: String) = tentDao.cloneRepository(url)
}