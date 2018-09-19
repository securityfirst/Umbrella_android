package org.secfirst.umbrella.whitelabel.data.database.reader

import javax.inject.Inject

class RssRepository @Inject constructor(private val rssDao: RssDao) : RssRepo {

    override suspend fun saveAllRss(rssList: List<RSS>) = rssDao.saveAll(rssList)

    override suspend fun delete(rss: RSS) = rssDao.delete(rss)

    override suspend fun saveRss(rss: RSS) = rssDao.save(rss)

    override suspend fun getAllRss() = rssDao.getAll()
}