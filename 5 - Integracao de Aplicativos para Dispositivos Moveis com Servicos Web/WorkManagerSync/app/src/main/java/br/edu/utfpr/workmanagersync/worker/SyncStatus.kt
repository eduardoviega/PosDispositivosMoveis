package br.edu.utfpr.workmanagersync.worker

import androidx.work.WorkInfo

data class SyncStatus(
    val state: String,
    val message: String,
    val lastUpdated: Long
) {
    companion object {
        fun fromWorkInfo(workInfo: WorkInfo?): SyncStatus {
            if (workInfo == null) return idle()
            
            return when (workInfo.state) {
                WorkInfo.State.ENQUEUED -> SyncStatus(
                    state = "ENQUEUED",
                    message = "Sincronização aguardando conexão",
                    lastUpdated = System.currentTimeMillis()
                )
                WorkInfo.State.RUNNING -> SyncStatus(
                    state = "RUNNING",
                    message = "Sincronizando dados",
                    lastUpdated = System.currentTimeMillis()
                )
                WorkInfo.State.SUCCEEDED -> SyncStatus(
                    state = "SUCCEEDED",
                    message = "Sincronização concluída",
                    lastUpdated = System.currentTimeMillis()
                )
                WorkInfo.State.FAILED -> SyncStatus(
                    state = "FAILED",
                    message = "Falha na sincronização",
                    lastUpdated = System.currentTimeMillis()
                )
                WorkInfo.State.CANCELLED -> SyncStatus(
                    state = "CANCELLED",
                    message = "Sincronização cancelada",
                    lastUpdated = System.currentTimeMillis()
                )
                WorkInfo.State.BLOCKED -> SyncStatus(
                    state = "BLOCKED",
                    message = "Sincronização bloqueada aguardando constraints",
                    lastUpdated = System.currentTimeMillis()
                )
            }
        }

        fun idle(): SyncStatus = SyncStatus(
            state = "IDLE",
            message = "Aguardando nova sincronização",
            lastUpdated = System.currentTimeMillis()
        )
    }
}

