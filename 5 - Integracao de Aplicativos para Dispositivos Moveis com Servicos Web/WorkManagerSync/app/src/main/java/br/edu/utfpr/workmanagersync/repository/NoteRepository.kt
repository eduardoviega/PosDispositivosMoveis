package br.edu.utfpr.workmanagersync.repository

import android.content.Context
import br.edu.utfpr.workmanagersync.data.AppDatabase
import br.edu.utfpr.workmanagersync.data.NoteEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class NoteRepository private constructor(context: Context) {

    private val noteDao = AppDatabase.getInstance(context).noteDao()
    private val firestore = FirebaseFirestore.getInstance()

    fun observeAllNotes(): Flow<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun createLocalNote(title: String, description: String): NoteEntity =
        withContext(Dispatchers.IO) {
            val note = NoteEntity(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                description = description.trim(),
                createdAt = System.currentTimeMillis(),
                isSynced = false
            )
            noteDao.insert(note)
            note
        }

    suspend fun getPendingNotesCount(): Int = withContext(Dispatchers.IO) {
        noteDao.getPendingNotes().size
    }

    suspend fun getSyncedNotesCount(): Int = withContext(Dispatchers.IO) {
        val allNotes = noteDao.getAllNotes()
        var syncedCount = 0
        allNotes.collect { notes ->
            syncedCount = notes.count { it.isSynced }
        }
        syncedCount
    }

    suspend fun syncPendingNotes(): Int = withContext(Dispatchers.IO) {
        val pendingNotes = noteDao.getPendingNotes()

        pendingNotes.forEach { note ->
            firestore.collection(COLLECTION_NOTES)
                .document(note.id)
                .set(note)
                .await()
            noteDao.markAsSynced(note.id)
        }

        pendingNotes.size
    }

    companion object {
        private const val COLLECTION_NOTES = "notes"

        @Volatile
        private var INSTANCE: NoteRepository? = null

        fun getInstance(context: Context): NoteRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NoteRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}