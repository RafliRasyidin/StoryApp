package com.rasyidin.storyapp.presentation.home

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.data.utils.onSuccess
import com.rasyidin.storyapp.databinding.FragmentStoryByLocationMapBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class StoryByLocationMapFragment :
    FragmentBinding<FragmentStoryByLocationMapBinding>(FragmentStoryByLocationMapBinding::inflate),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var locationRequest: LocationRequest

    private var deviceLocation: Location? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.getStories(withLocation = 1)
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        observeStories()

        createLocationRequest()
    }

    private fun observeStories() {
        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stories.collect { resultState ->
                    resultState.onSuccess { stories ->
                        stories?.map { story ->
                            val latitude = story.lat as Double
                            val longitude = story.lng as Double
                            val latLng = LatLng(latitude, longitude)
                            map.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                            )
                        }
                        animateCamera()
                    }
                }
            }
        }
    }

    private fun animateCamera() {
        val latLng = LatLng(LAT_INDO, LNG_INDO)
        map.setOnMapLoadedCallback {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5F))
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[ACCESS_FINE_LOCATION] ?: false -> getDeviceLocation()
            permissions[ACCESS_COARSE_LOCATION] ?: false -> getDeviceLocation()
        }
    }

    private fun getDeviceLocation() {
        if (isPermissionsGranted(ACCESS_FINE_LOCATION) && isPermissionsGranted(
                ACCESS_COARSE_LOCATION
            )
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    deviceLocation = location
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {}
            RESULT_CANCELED -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.gps_warning_2),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (exception: IntentSender.SendIntentException) {
                        Toast.makeText(
                            requireActivity(),
                            exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun isPermissionsGranted(permission: String) = ContextCompat.checkSelfPermission(
        requireActivity(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val LAT_INDO = -6.200000
        const val LNG_INDO = 106.816666

        val TAG = StoryByLocationMapFragment::class.simpleName
    }

}