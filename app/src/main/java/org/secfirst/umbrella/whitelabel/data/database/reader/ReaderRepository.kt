package org.secfirst.umbrella.whitelabel.data.database.reader

import javax.inject.Inject

class ReaderRepository @Inject constructor(private val readerDao: ReaderDao) : ReaderRepo {

    override suspend fun deleteLocation() = readerDao.deleteLocation()

    override suspend fun saveFeedLocation(feedLocation: FeedLocation) = readerDao.saveFeedLocation(feedLocation)

    override suspend fun saveAllFeedSources(feedSources: List<FeedSource>) = readerDao.saveAllFeedSource(feedSources)

    override suspend fun getFeedLocation() = readerDao.getFeedLocation()

    override suspend fun getAllFeedSources() = readerDao.getAllFeedSource()

    override suspend fun saveAllRss(rssList: List<RSS>) = readerDao.saveAllRss(rssList)

    override suspend fun delete(rss: RSS) = readerDao.delete(rss)

    override suspend fun saveRss(rss: RSS) = readerDao.saveRss(rss)

    override suspend fun getAllRss() = readerDao.getAllRss()
}