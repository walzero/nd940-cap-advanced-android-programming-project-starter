package com.example.android.politicalpreparedness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.response.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class CivicInfoRepository(
    private val electionDatabase: ElectionDatabase,
    private val civicsApi: CivicsApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _representatives = MutableLiveData<List<Representative>>()

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

    suspend fun refreshRepresentatives(address: Address) {
        try {
            val response = civicsApi.retrofitService
                .queryRepresentatives(address.toFormattedString())

            _representatives.postValue(response.toRepresentatives())
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun voterInformation(
        division: Division,
        electionId: Int?
    ): VoterInfoResponse? {
        val divisionStateValue = if (division.state.isBlank()) "ca" else division.state

        val queryString = StringBuilder("country:${division.country}")
            .append("/state:$divisionStateValue")

        return try {
            civicsApi.retrofitService.voterInfo(
                address = queryString.toString(),
                electionId = electionId?.toLong() ?: 0L
            )
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun upcomingElections() = electionDatabase.electionDao.getElections()

    fun representatives(): LiveData<List<Representative>> = _representatives
}