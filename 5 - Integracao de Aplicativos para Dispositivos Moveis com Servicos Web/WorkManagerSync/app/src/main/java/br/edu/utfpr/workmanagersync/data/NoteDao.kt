package br.edu.utfpr.workmanagersync.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isSynced = 0 ORDER BY createdAt ASC")
    suspend fun getPendingNotes(): List<NoteEntity>

    @Query("UPDATE notes SET isSynced = 1 WHERE id = :noteId")
    suspend fun markAsSynced(noteId: String)
}