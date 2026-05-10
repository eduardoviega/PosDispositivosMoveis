package br.edu.utfpr.workmanagersync.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.edu.utfpr.workmanagersync.repository.NoteRepository
import kotlinx.coroutines.CancellationException

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Iniciando sincronização pendente com Firestore.")

        return try {
            val syncedCount = NoteRepository.getInstance(applicationContext).syncPendingNotes()
            if (syncedCount > 0) {
                Log.d(TAG, "Sincronização concluída: $syncedCount anotações sincronizadas")
            } else {
                Log.d(TAG, "Nenhuma anotação pendente para sincronizar")
            }
            Result.success()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Log.e(TAG, "Falha na sincronização. Solicitando retry automático.", exception)
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
    }
}