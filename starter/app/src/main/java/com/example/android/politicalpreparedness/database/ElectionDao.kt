package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg election: Election)

    @Query("SELECT * FROM election_table ORDER BY electionDay DESC")
    fun getElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE followed ORDER BY electionDay DESC")
    fun getFollowedElections(): LiveData<List<Election>>

    @Query("SELECT * FROM election_table WHERE id = :id")
    fun getElectionById(id: Int): LiveData<Election>

    @Update
    fun updateItems(vararg election: Election)

    @Delete
    suspend fun deleteElections(vararg election: Election): Int

    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections(): Int

}