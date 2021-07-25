package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.extension.isAllowed
import com.example.android.politicalpreparedness.extension.showConfirmationSnackbar
import com.example.android.politicalpreparedness.extension.showShortToast
import com.example.android.politicalpreparedness.extension.verifyLocationSettings
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
    }

    private lateinit var binding: FragmentRepresentativeBinding

    private val viewModel: RepresentativeViewModel by viewModel()

    private val adapter by lazy { RepresentativeListAdapter() }

    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION

    private val locationSettings by lazy { LocationServices.getSettingsClient(requireContext()) }

    private val locationServices by lazy {
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }

    private val fineLocationRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            when (result) {
                true -> runWithGPSEnabled { searchWithLastKnownLocation() }
                else -> showShortToast(R.string.location_required)
            }
        }

    private val gpsRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> viewModel.fetchRepresentativesFromCurrentLocation()
                else -> viewModel.onGpsDisabled()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.representativesAdapter = adapter
        binding.lifecycleOwner = this

        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    private fun setObservers() = with(viewModel) {
        representatives.observe(viewLifecycleOwner) { rep -> rep?.let { adapter.submitList(it) } }
        showSnackBar.observe(viewLifecycleOwner) {
            binding.root.showConfirmationSnackbar(getString(it), getString(R.string.ok))
        }
    }

    private fun setListeners() = with(binding) {
        buttonLocation.setOnClickListener { onSearchRepresentativeByLocation() }
    }

    private fun onSearchRepresentativeByLocation() {
        when (isAllowed(fineLocation)) {
            true -> runWithGPSEnabled { searchWithLastKnownLocation() }
            false -> fineLocationRequest.launch(fineLocation)
        }
    }

    private fun runWithGPSEnabled(onGPSEnabled: () -> Unit) {
        locationSettings.verifyLocationSettings(
            onSuccessListener = { onGPSEnabled() }
        ) { exception -> onGPSNotEnabled(exception) }
    }

    private fun onGPSNotEnabled(exception: Exception) = when (exception) {
        is ResolvableApiException ->
            gpsRequest.launch(IntentSenderRequest.Builder(exception.resolution).build())

        else -> viewModel.onGpsDisabled()
    }

    @SuppressLint("MissingPermission")
    private fun searchWithLastKnownLocation() {
        locationServices.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = geoCodeLocation(task.result)
                viewModel.updateRepresentatives(location)
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare.orEmpty(),
                    address.subThoroughfare.orEmpty(),
                    address.locality.orEmpty(),
                    address.adminArea.orEmpty(),
                    address.postalCode.orEmpty()
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}