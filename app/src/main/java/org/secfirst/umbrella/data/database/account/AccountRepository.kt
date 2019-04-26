package org.secfirst.umbrella.data.database.account

import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import javax.inject.Inject

class AccountRepository @Inject constructor(private val accountDao: AccountDao) : AccountRepo {

    override suspend fun wipeMainContent() = accountDao.deleteMainContent()

    override suspend fun loginDatabase(userToken: String) = accountDao.initDatabase(userToken)

    override suspend fun changeToken(userToken: String) = accountDao.changeDatabaseAccess(userToken)

    override suspend fun saveFeedLocation(feedLocation: FeedLocation) = accountDao.saveFeedLocation(feedLocation)

    override suspend fun saveAllFeedSources(feedSources: List<FeedSource>) = accountDao.saveAllFeedSource(feedSources)

    override suspend fun getFeedLocation() = accountDao.getFeedLocation()

    override suspend fun getAllFeedSources() = accountDao.getAllFeedSource()

}