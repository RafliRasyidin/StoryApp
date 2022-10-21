package com.rasyidin.storyapp.presentation.home.post_story

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.data.utils.onFailure
import com.rasyidin.storyapp.data.utils.onLoading
import com.rasyidin.storyapp.data.utils.onSuccess
import com.rasyidin.storyapp.databinding.FragmentAddStoryBinding
import com.rasyidin.storyapp.presentation.component.DialogAskPostWithLocation
import com.rasyidin.storyapp.presentation.component.DialogSuccess
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.utils.reduceAndRotateFileImageCamera
import com.rasyidin.storyapp.presentation.utils.reduceFileImageGallery
import com.rasyidin.storyapp.presentation.utils.rotateBitmap
import com.rasyidin.storyapp.presentation.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AddStoryFragment :
    FragmentBinding<FragmentAddStoryBinding>(FragmentAddStoryBinding::inflate) {

    private val args: AddStoryFragmentArgs by navArgs()
    private var getFile: File? = null

    private val viewModel: PostStoryViewModel by viewModels()

    private var isImageGallery = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback

    private var deviceLocation: Location? = null

    private var isSendLocation = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.image != null) {
            getFile = args.image
        }

        if (!isPermissionGranted(REQUIRED_PERMISSIONS_CAMERA.first())) requestPermissionCamera()

        onView()

        setImagePreviewFromCamera()

        observePostStory()

        postStory()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (savedInstanceState != null) {
            isSendLocation = savedInstanceState.getBoolean(SEND_LOCATION_STATE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SEND_LOCATION_STATE, isSendLocation)
    }

    private fun onView() {
        binding.apply {
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnCamera.setOnClickListener {
                startCamera()
            }

            btnGallery.setOnClickListener {
                openGallery()
            }

            btnAddLocation.setOnClickListener {
                showDialogPostWithLocation()
            }
        }
    }

    private fun hideImagePlaceHolder() {
        binding.imgStoryPlaceholder.visibility = View.GONE
    }

    private fun showImagePlaceHolder() {
        binding.imgStoryPlaceholder.visibility = View.VISIBLE
    }

    private fun setImagePreviewFromCamera() {
        val isImageFileExist = getFile != null
        val isBackCamera = args.cameraSelector
        if (isImageFileExist) {
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )
            isImageGallery = false
            binding.imgStory.setImageBitmap(result)
            hideImagePlaceHolder()
        } else {
            showImagePlaceHolder()
            binding.imgStory.setImageBitmap(null)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage = result?.data?.data as Uri
            val myFile = uriToFile(selectedImage, requireActivity())
            getFile = myFile
            isImageGallery = true
            binding.imgStory.setImageBitmap(BitmapFactory.decodeFile(getFile?.path))
            hideImagePlaceHolder()
        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            action = ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, getString(R.string.choose_pict))
        launcherIntentGallery.launch(chooser)
    }

    private fun postStory() {
        binding.btnPost.setOnClickListener {
            val isCaptionFilled = binding.etCaption.text.toString().isNotBlank()
            val isPictTaken = getFile != null
            if (isCaptionFilled && isPictTaken) {
                val imageFile = if (isImageGallery) {
                    reduceFileImageGallery(getFile as File)
                } else {
                    reduceAndRotateFileImageCamera(getFile as File)
                }
                val description = binding.etCaption.text.toString()
                if (isSendLocation) {
                    val latitude = deviceLocation?.latitude as Double
                    val longitude = deviceLocation?.longitude as Double
                    viewModel.postStory(description, imageFile, latitude, longitude)
                } else {
                    viewModel.postStory(description, imageFile)
                }
                binding.progressBar.isVisible = true
            }
        }

    }

    private fun observePostStory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postStory.collect { result ->

                    withContext(Dispatchers.Main) {
                        result.onLoading {
                            binding.progressBar.isVisible = true
                        }

                        result.onSuccess {
                            binding.progressBar.isVisible = false
                            showDialogSuccess()
                        }

                        result.onFailure { message ->
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.error_post),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, message)

                        }
                    }
                }
            }
        }
    }

    private fun showDialogSuccess() {
        if (!isAdded) return
        if (childFragmentManager.findFragmentByTag(DialogSuccess.TAG) == null) {
            val dialogSuccess = DialogSuccess.newInstance()
            dialogSuccess.show(childFragmentManager, DialogSuccess.TAG)

            dialogSuccess.onClickOk = {
                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment())
            }

            dialogSuccess.onDialogDismiss = { isButtonClicked ->
                if (!isButtonClicked) {
                    findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment())
                }
            }
        }
    }

    private fun showDialogPostWithLocation() {
        if (childFragmentManager.findFragmentByTag(DialogAskPostWithLocation.TAG) == null) {
            val dialogAskPostWithLocation = DialogAskPostWithLocation.newInstance()
            dialogAskPostWithLocation.show(childFragmentManager, DialogAskPostWithLocation.TAG)

            createLocationRequest()
            createLocationCallback()

            dialogAskPostWithLocation.onClickYes = {
                getDeviceLocation()
            }

            dialogAskPostWithLocation.onClickCancel = {
                binding.animLocation.apply {
                    /*stopLocationUpdates()*/
                    visibility = View.GONE
                    cancelAnimation()
                }
            }
        }
    }

    private val requestPermissionLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: false -> getDeviceLocation()
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                    ?: false -> getDeviceLocation()
                else -> {}
            }
        }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                createLocationRequest()
                createLocationCallback()
                binding.animLocation.apply {
                    visibility = View.VISIBLE
                    playAnimation()
                }
            }
            RESULT_CANCELED -> {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.gps_warning),
                    Toast.LENGTH_SHORT
                ).show()
                isSendLocation = false
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getDeviceLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendException: IntentSender.SendIntentException) {
                        Toast.makeText(
                            requireActivity(),
                            sendException.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS_CAMERA -> {
                if (!isPermissionGranted(REQUIRED_PERMISSIONS_CAMERA.first())) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.suggest_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
            REQUEST_CODE_PERMISSIONS_LOCATION -> {

            }
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                deviceLocation = locationResult.lastLocation
                isSendLocation = true
                Log.d(TAG, deviceLocation.toString())
                binding.animLocation.apply {
                    visibility = View.VISIBLE
                    playAnimation()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (isPermissionGranted(ACCESS_FINE_LOCATION) && isPermissionGranted(ACCESS_COARSE_LOCATION)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    deviceLocation = location
                    isSendLocation = true
                    Log.d(TAG, deviceLocation.toString())
                    binding.animLocation.apply {
                        visibility = View.VISIBLE
                        playAnimation()
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLocationLauncher.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun requestPermissionCamera() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS_CAMERA,
            REQUEST_CODE_PERMISSIONS_CAMERA
        )
    }

    private fun isPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED


    private fun startCamera() {
        findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToCameraFragment())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private val REQUIRED_PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS_CAMERA = 10
        private const val REQUEST_CODE_PERMISSIONS_LOCATION = 11
        private const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        private val TAG = AddStoryFragment::class.simpleName

        private const val SEND_LOCATION_STATE = "locationState"
    }
}