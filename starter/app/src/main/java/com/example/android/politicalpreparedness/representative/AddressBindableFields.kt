package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.MutableLiveData

interface AddressBindableFields {
    val addressLine1: MutableLiveData<String>
    val addressLine2: MutableLiveData<String>
    val city: MutableLiveData<String>
    val zipcode: MutableLiveData<String>
}