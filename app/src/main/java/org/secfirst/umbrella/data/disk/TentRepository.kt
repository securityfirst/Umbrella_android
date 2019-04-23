package org.secfirst.umbrella.data.disk

import java.io.File
import javax.inject.Inject

class TentRepository @Inject constructor(private val tentDao: TentDao) : TentRepo {


    override fun loadFormFile(): List<File> = tentDao.filterForms()

    override suspend fun updateRepository() = tentDao.rebaseBranch()

    override suspend fun loadElementsFile() = tentDao.filterCategories()

    override fun loadFile() = tentDao.filterElements()

    override suspend fun fetchRepository(url: String) = tentDao.cloneRepository(url)
}