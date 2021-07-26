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
import timber.log.Timber

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
    override val state = MutableLiveData<String>()
    override val zipcode = MutableLiveData<String>()

    fun updateRepresentatives(address: Address? = null) = viewModelScope.launch {
        Timber.d("Fun Fields: ${joinFieldsToAddress()}")
        Timber.d("Obj Fields: $address")
        address?.let { updateAddressFields(it) }
        _repository.refreshRepresentatives(address ?: joinFieldsToAddress())
    }

    fun onGpsDisabled() =
        _showSnackBar.postValue(R.string.gps_required)

    private fun updateAddressFields(address: Address) {
        address.line1.let { addressLine1.postValue(it) }
        address.line2.let { addressLine2.postValue(it) }
        address.city.let { city.postValue(it) }
        address.state.let { state.postValue(it) }
        address.zip.let { zipcode.postValue(it) }
    }

    private fun joinFieldsToAddress(): Address = Address(
        line1 = addressLine1.value.orEmpty(),
        line2 = addressLine2.value.orEmpty(),
        city = city.value.orEmpty(),
        state = state.value.orEmpty(),
        zip = zipcode.value.orEmpty()
    )

}
