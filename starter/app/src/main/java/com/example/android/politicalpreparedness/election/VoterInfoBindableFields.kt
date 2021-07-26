package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData

interface VoterInfoBindableFields {
    val electionName: LiveData<String>
    val electionDay: LiveData<String>
    fun onStateLocationsInfoClick()
    fun onBallotInfoClick()
    fun onFollowClick()
}