package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.extension.SingleLiveEvent

interface VoterInfoBindableFields {
    val navigateToUrl: SingleLiveEvent<String?>
    val enableInfoButtons: LiveData<Boolean>
    val electionName: LiveData<String>
    val electionDay: LiveData<String>
    fun onStateLocationsInfoClick()
    fun onBallotInfoClick()
    fun onFollowUnfollowClick()
}