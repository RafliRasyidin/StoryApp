package com.rasyidin.storyapp.presentation.home.post_story

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.databinding.FragmentCameraBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.utils.createCustomTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : FragmentBinding<FragmentCameraBinding>(FragmentCameraBinding::inflate) {

    private lateinit var cameraExecutor: ExecutorService

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        onView()
    }

    private fun onView() {
        binding.apply {
            cardCamera.setOnClickListener {
                takePhoto()
            }

            imgSwitchCamera.setOnClickListener {
                switchCamera()
            }
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(requireActivity())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    lifecycleScope.launchWhenCreated {
                        withContext(Dispatchers.Main) {
                            findNavController().navigate(
                                CameraFragmentDirections.actionCameraFragmentToAddStoryFragment(
                                    photoFile,
                                    cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                                )
                            )
                        }
                    }

                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.error_take_pict),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, exception.printStackTrace().toString())
                }
            }
        )
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.error_camera),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.e(TAG, "exception: ${e.printStackTrace()}")
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    companion object {
        private val TAG = CameraFragment::class.simpleName
    }
}