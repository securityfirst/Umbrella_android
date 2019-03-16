package org.secfirst.umbrella.whitelabel.feature.content

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.goncalves.pugnotification.notification.PugNotification
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.TextProgressMonitor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.content.ContentDao
import org.secfirst.umbrella.whitelabel.data.database.content.createDefaultRSS
import org.secfirst.umbrella.whitelabel.data.database.content.createFeedSources
import org.secfirst.umbrella.whitelabel.data.disk.*
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper.Companion.PREF_NAME
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.isInternetConnected
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import org.secfirst.umbrella.whitelabel.serialize.ElementLoader
import org.secfirst.umbrella.whitelabel.serialize.ElementSerialize
import org.secfirst.umbrella.whitelabel.serialize.ElementSerializeMonitor
import java.io.File
import java.io.PrintWriter
import java.util.*


class ContentService : Service(), ElementSerializeMonitor {

    private val contentDao
        get() = object : ContentDao {
            override fun onContentProgress(percentage: Int) {
                val calPercentage = percentage / 3
                val base = 66
                sendMessage(base + calPercentage, getString(R.string.notification_update_database))
                if (percentage >= 100)
                    processCompleted()
            }
        }
    private val tentDao
        get() = object : TentDao {}

    private val tentRepo = TentRepository(tentDao)
    private val elementSerialize = ElementSerialize(tentRepo, this)
    private val elementLoader = ElementLoader(tentRepo)

    private val tentProgressMonitor = object : TextProgressMonitor(PrintWriter(System.out)) {
        override fun onUpdate(taskName: String, cmp: Int, totalWork: Int, pcnt: Int) {
            sendMessage(pcnt, getString(R.string.notification_fetching_data))
        }
    }

    private fun startForegroundService(url: String) {
        launchSilent(uiContext) {
            val isCloned = if (isInternetConnected()) cloneRepository(url) else false
            if (isCloned) {
                val element = elementLoader.load(elementSerialize.process())
                contentDao.insertAllLessons(element)
                contentDao.insertFeedSource(createFeedSources())
                contentDao.insertDefaultRSS(createDefaultRSS())
            } else {
                sendNoConnectionMessage()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            val url = intent.extras?.getString(EXTRA_URL_REPOSITORY) ?: ""
            when (action) {
                ACTION_START_FOREGROUND_SERVICE -> startForegroundService(url)
                ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun cloneRepository(url: String): Boolean {
        var result = true
        try {
            withContext(ioContext) {
                if (isNotRepository() && url.isNotBlank()) {
                    Git.cloneRepository()
                            .setURI(url)
                            .setDirectory(File(getPathRepository()))
                            .setBranchesToClone(Arrays.asList(BRANCH_NAME))
                            .setProgressMonitor(tentProgressMonitor)
                            .setBranch(BRANCH_NAME)
                            .call()
                }
            }
        } catch (e: Exception) {
            result = false
            releaseService()
            sendLostConnectionMessage()
        }
        return result
    }

    override fun onSerializeProgress(percentage: Int) {
        val calPercentage = percentage / 3
        val base = 33
        sendMessage(base + calPercentage, getString(R.string.notification_update_database))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        releaseService()
        super.onDestroy()
    }

    private fun processCompleted() {
        val messageInt = Intent(EXTRA_CONTENT_SERVICE_ID)
        messageInt.putExtra(ACTION_COMPLETED_FOREGROUND_SERVICE, true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageInt)
        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        preferences.edit().putBoolean(EXTRA_STATE_PROCESS, true).apply()
        stopForegroundService()
    }

    private fun sendNoConnectionMessage() {
        val messageInt = Intent(EXTRA_CONTENT_SERVICE_ID)
        messageInt.putExtra(ACTION_UNKNOWN_ERROR, true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageInt)
    }

    private fun sendMessage(percentage: Int, title: String) {
        val messageInt = Intent(EXTRA_CONTENT_SERVICE_ID)
        messageInt.putExtra(EXTRA_CONTENT_SERVICE_PROGRESS, percentage)
        messageInt.putExtra(EXTRA_CONTENT_SERVICE_TITLE_PROGRESS, title)
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageInt)
    }

    private fun sendLostConnectionMessage() {
        val messageInt = Intent(EXTRA_CONTENT_SERVICE_ID)
        messageInt.putExtra(ACTION_LOST_CONNECTION, true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageInt)
    }

    private fun createNotificationService() {
        PugNotification.with(this@ContentService)
                .load()
                .title(getString(R.string.notification_fetching_data))
                .identifier(NOTIFICATION_IDENTIFY)
                .smallIcon(R.drawable.umbrella190)
                .progress()
                .build()
    }

    private fun updateNotificationService(title: String, progress: Int) {
        PugNotification.with(this@ContentService)
                .load()
                .title(title)
                .identifier(NOTIFICATION_IDENTIFY)
                .progress()
                .update(NOTIFICATION_IDENTIFY, progress, 100, false)
                .build()
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    private fun releaseService() {
        val preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        val isFinishProcess = preferences.getBoolean(EXTRA_STATE_PROCESS, false)
        if (!isFinishProcess)
            File(getPathRepository()).deleteRecursively()
        stopForegroundService()
    }


    companion object {
        private const val NOTIFICATION_IDENTIFY = 1
        const val EXTRA_STATE_PROCESS = "extra_process"
        const val EXTRA_CONTENT_SERVICE_ID = "content_id"
        const val EXTRA_CONTENT_SERVICE_PROGRESS = "content_progress"
        const val EXTRA_CONTENT_SERVICE_TITLE_PROGRESS = "content_title_progress"

        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_COMPLETED_FOREGROUND_SERVICE = "ACTION_COMPLETED_FOREGROUND_SERVICE"
        const val ACTION_UNKNOWN_ERROR = "ACTION_UNKNOWN_ERROR"
        const val ACTION_LOST_CONNECTION = "ACTION_LOST_CONNECTION"
    }
}