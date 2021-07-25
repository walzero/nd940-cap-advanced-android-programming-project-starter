package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CivicInfoRepository(
    private val electionDatabase: ElectionDatabase,
    private val civicsApi: CivicsApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun refreshUpcomingElections() {
        withContext(ioDispatcher) {
            try {
                val result = civicsApi.retrofitService.queryElections()
                electionDatabase.electionDao.insert(*result.elections.toTypedArray())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun upcomingElections() = electionDatabase.electionDao.getElections()
}