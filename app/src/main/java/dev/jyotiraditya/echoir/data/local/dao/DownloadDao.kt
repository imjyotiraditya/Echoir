package dev.jyotiraditya.echoir.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.jyotiraditya.echoir.domain.model.Download
import dev.jyotiraditya.echoir.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads WHERE status IN (:statuses) ORDER BY timestamp DESC")
    fun getDownloadsByStatus(statuses: List<DownloadStatus>): Flow<List<Download>>

    @Query("SELECT * FROM downloads WHERE albumId = :albumId")
    suspend fun getDownloadsByAlbumId(albumId: Long): List<Download>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: Download)

    @Update
    suspend fun update(download: Download)

    @Delete
    suspend fun delete(download: Download)

    @Query("UPDATE downloads SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: DownloadStatus)

    @Query("UPDATE downloads SET filePath = :filePath WHERE id = :id")
    suspend fun updateFilePath(id: Long, filePath: String)

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): Download?

    @Query("DELETE FROM downloads WHERE status = :status")
    suspend fun deleteByStatus(status: DownloadStatus)
}