package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.CivicInfoRepository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(
    private val civicInfoRepository: CivicInfoRepository
) : ViewModel() {

    init {
        refreshElections()
    }

    val upcomingElections: LiveData<List<Election>>
        get() = civicInfoRepository.upcomingElections()

    val followedElections: LiveData<List<Election>>
        get() = civicInfoRepository.followedElections()


    private fun refreshElections() = viewModelScope.launch {
        civicInfoRepository.refreshUpcomingElections()
    }
}