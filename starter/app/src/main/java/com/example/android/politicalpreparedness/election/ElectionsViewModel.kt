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

    val upcomingElection: LiveData<List<Election>>
        get() = civicInfoRepository.upcomingElections()

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    private fun refreshElections() = viewModelScope.launch {
        civicInfoRepository.refreshUpcomingElections()
    }
}