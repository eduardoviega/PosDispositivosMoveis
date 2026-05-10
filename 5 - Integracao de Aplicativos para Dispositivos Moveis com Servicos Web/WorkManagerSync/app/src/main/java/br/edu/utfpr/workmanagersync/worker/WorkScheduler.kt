package br.edu.utfpr.workmanagersync.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import java.util.concurrent.TimeUnit

object WorkScheduler {
    private const val UNIQUE_WORK_NAME = "sync_notes"
    private const val TAG = "WorkScheduler"

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
        Log.d(TAG, "Trabalho de sincronização agendado: $UNIQUE_WORK_NAME")
    }

    fun observeSyncWork(context: Context): Flow<SyncStatus> {
        val liveData = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)

        return liveData.map { workInfoList ->
            val workInfo = workInfoList.firstOrNull()
            SyncStatus.fromWorkInfo(workInfo)
        }.asFlow().conflate()
    }
}