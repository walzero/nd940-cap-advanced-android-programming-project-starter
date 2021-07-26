package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.CivicInfoRepository
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.response.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: CivicInfoRepository
) : ViewModel(), VoterInfoBindableFields {

    private var electionId: Int? = null
    private var division: Division? = null

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()

    override val electionName: LiveData<String> =
        Transformations.map(_voterInfo) { it.election.name }

    override val electionDay: LiveData<String> =
        Transformations.map(_voterInfo) { it.election.electionDay.toString() }

    override fun onStateLocationsInfoClick() {}

    override fun onBallotInfoClick() {}

    override fun onFollowClick() {}

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    fun fetchVoterInfo(
        electionId: Int,
        division: Division
    ) = viewModelScope.launch {
        this@VoterInfoViewModel.electionId = electionId
        this@VoterInfoViewModel.division = division
        _voterInfo.postValue(repository.voterInformation(division, electionId))
    }

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}