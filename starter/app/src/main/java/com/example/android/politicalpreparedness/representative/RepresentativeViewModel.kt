package com.example.android.politicalpreparedness.representative

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.CivicInfoRepository
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val _repository: CivicInfoRepository
) : ViewModel() {

    private val _showSnackBar = MutableLiveData<@StringRes Int>()
    val showSnackBar: LiveData<Int>
        get() = _showSnackBar

    val representatives: LiveData<List<Representative>>
        get() = _repository.representatives()

    fun updateRepresentatives(address: Address) = viewModelScope.launch {
        _repository.refreshRepresentatives(address)
    }

    fun onGpsDisabled() =
        _showSnackBar.postValue(R.string.gps_required)

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location
    fun fetchRepresentativesFromCurrentLocation() {

    }

    //TODO: Create function to get address from individual fields

}
