package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.extension.isAllowed
import com.example.android.politicalpreparedness.extension.showConfirmationSnackbar
import com.example.android.politicalpreparedness.extension.showShortToast
import com.example.android.politicalpreparedness.extension.verifyLocationSettings
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentRepresentativeBinding

    private val viewModel: RepresentativeViewModel by viewModel()

    private val adapter by lazy { RepresentativeListAdapter() }

    private val geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }

    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION

    private val locationSettings by lazy { LocationServices.getSettingsClient(requireContext()) }

    private val locationServices by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
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
                Activity.RESULT_OK -> searchWithLastKnownLocation()
                else -> viewModel.onGpsDisabled()
            }
        }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (locationResult == null) {
                showShortToast(R.string.location_error)
                return
            }

            locationResult.locations.first()
            val location = geoCodeLocation(locationResult.locations.first())
            viewModel.updateRepresentatives(location)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepresentativeBinding.inflate(layoutInflater)
        binding.representativesAdapter = adapter
        binding.addressFields = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationUpdates()
    }

    private fun setObservers() = with(viewModel) {
        representatives.observe(viewLifecycleOwner) { rep -> rep?.let { adapter.submitList(it) } }
        showSnackBar.observe(viewLifecycleOwner) {
            binding.root.showConfirmationSnackbar(getString(it), getString(R.string.ok))
        }
    }

    private fun setListeners() = with(binding) {
        buttonLocation.setOnClickListener { onSearchRepresentativeByLocation() }
        buttonSearch.setOnClickListener { onButtonSearchClicked() }
        state.onItemSelectedListener = createSpinnerSelectionListener()
        recyclerviewRepresentatives.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            createMotionController(scrollY - oldScrollY)
        }
    }

    private fun onButtonSearchClicked() {
        viewModel.updateRepresentatives()
        hideKeyboard()
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
            if (task.isSuccessful && (task.result != null)) {
                val location = geoCodeLocation(task.result)
                viewModel.updateRepresentatives(location)
            } else {
                startLocationUpdates()
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

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest.create().apply {
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
            interval = 5000;
            fastestInterval = 5000;
        }

        locationServices.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() = try {
        locationServices.removeLocationUpdates(locationCallback)
    } catch (e: Exception) {
        Timber.e(e)
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun toggleMotionEnabledState(enabled: Boolean) {
        binding.motionLayoutRepresentatives
            .getTransition(R.id.hideForm)?.isEnabled = enabled
    }

    private fun createSpinnerSelectionListener() = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val stateName = parent?.getItemAtPosition(position) as String
            viewModel.state.value = stateName
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun createMotionController(dy: Int) {
        val layoutManager =
            (binding.recyclerviewRepresentatives.layoutManager as LinearLayoutManager)

        val pastVisiblesItems = layoutManager.findFirstCompletelyVisibleItemPosition()

        synchronized(pastVisiblesItems) {
            Timber.e("LOG SCROLL pastVisiblesItems: $pastVisiblesItems, dy: $dy")
            when {
                dy > 0 && pastVisiblesItems > 8 -> toggleMotionEnabledState(false)

                dy < 0 && pastVisiblesItems <= 2 -> toggleMotionEnabledState(true)

                else -> {
                }
            }
        }
    }
}