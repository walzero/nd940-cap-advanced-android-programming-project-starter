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
) : ViewModel(), AddressBindableFields {

    private val _showSnackBar = MutableLiveData<@StringRes Int>()
    val showSnackBar: LiveData<Int>
        get() = _showSnackBar

    val representatives: LiveData<List<Representative>>
        get() = _repository.representatives()

    override val addressLine1 = MutableLiveData<String>()
    override val addressLine2 = MutableLiveData<String>()
    override val city = MutableLiveData<String>()
    override val zipcode = MutableLiveData<String>()

    fun updateRepresentatives(address: Address) = viewModelScope.launch {
        _repository.refreshRepresentatives(address)
    }

    fun onGpsDisabled() =
        _showSnackBar.postValue(R.string.gps_required)

    //TODO: Create function get address from geo location
    fun fetchRepresentativesFromCurrentLocation() {

    }

    //TODO: Create function to get address from individual fields
    private fun joinFieldsToAddress(): Address = Address(
        line1 = addressLine1.value.orEmpty(),
        line2 = addressLine2.value.orEmpty(),
        city = city.value.orEmpty(),
        state = "",
        zip = zipcode.value.orEmpty()
    )

}
