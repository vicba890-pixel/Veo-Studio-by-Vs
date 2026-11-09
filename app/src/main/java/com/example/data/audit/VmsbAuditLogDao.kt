package com.example.data.audit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VmsbAuditLogDao {

    @Query("SELECT * FROM vmsb_audit_logs ORDER BY timestampMillis DESC")
    fun getAllAuditLogs(): Flow<List<VmsbAuditLogEntity>>

    @Query("SELECT * FROM vmsb_audit_logs WHERE eventType = :type ORDER BY timestampMillis DESC")
    fun getLogsByEventType(type: String): Flow<List<VmsbAuditLogEntity>>

    @Query("SELECT * FROM vmsb_audit_logs ORDER BY timestampMillis DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<VmsbAuditLogEntity>>

    @Query("SELECT * FROM vmsb_audit_logs ORDER BY timestampMillis DESC LIMIT :limit")
    suspend fun getRecentLogsSync(limit: Int): List<VmsbAuditLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VmsbAuditLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<VmsbAuditLogEntity>)

    @Query("DELETE FROM vmsb_audit_logs WHERE id NOT IN (SELECT id FROM vmsb_audit_logs ORDER BY timestampMillis DESC LIMIT :keepCount)")
    suspend fun pruneOldLogs(keepCount: Int = 500)

    @Query("DELETE FROM vmsb_audit_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM vmsb_audit_logs")
    suspend fun getCount(): Int
}
