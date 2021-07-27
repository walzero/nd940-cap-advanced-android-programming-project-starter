package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.CivicInfoRepository
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.extension.SingleLiveEvent
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: CivicInfoRepository,
    electionId: Int,
    division: Division
) : ViewModel(), VoterInfoBindableFields {

    init {
        fetchVoterInformationObject(division, electionId)
    }

    private val _voterInfo = repository.voterInfoResponse

    val election = repository.getElectionById(electionId)

    override val enableInfoButtons = Transformations.map(_voterInfo) { it != null }

    override val navigateToUrl = SingleLiveEvent<String?>()

    override val electionName: LiveData<String> =
        Transformations.map(_voterInfo) { it?.election?.name }

    override val electionDay: LiveData<String> =
        Transformations.map(_voterInfo) { it?.election?.electionDay?.toString() }

    override fun onStateLocationsInfoClick() {
        _voterInfo.value?.state.let { states ->
            states?.takeUnless { it.isEmpty() }?.get(0)
                ?.electionAdministrationBody
                ?.votingLocationFinderUrl?.let { navigateToUrl.postValue(it) }
        }
    }

    override fun onBallotInfoClick() {
        _voterInfo.value?.state.let { states ->
            states?.takeUnless { it.isEmpty() }?.get(0)
                ?.electionAdministrationBody?.ballotInfoUrl?.let { navigateToUrl.postValue(it) }
        }
    }

    override fun onFollowUnfollowClick() {
        viewModelScope.launch {
            election?.value?.let { currElection ->
                when (currElection.followed) {
                    true -> repository.unfollowElections(currElection)
                    false -> repository.followElections(currElection)
                }
            }
        }
    }

    private fun fetchVoterInformationObject(
        division: Division, electionId: Int
    ) = viewModelScope.launch { repository.updateVoterInformation(division, electionId) }

    fun getFollowButtonText(it: Election) = when (it.followed) {
        false -> R.string.follow_election
        true -> R.string.unfollow_election
    }

//TODO: Add var and methods to save and remove elections to local database
//TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
}