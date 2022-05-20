package com.rasyidin.storyapp.presentation.home.post_story

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.data.utils.onFailure
import com.rasyidin.storyapp.data.utils.onLoading
import com.rasyidin.storyapp.data.utils.onSuccess
import com.rasyidin.storyapp.databinding.FragmentAddStoryBinding
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

@AndroidEntryPoint
class AddStoryFragment :
    FragmentBinding<FragmentAddStoryBinding>(FragmentAddStoryBinding::inflate) {

    private val args: AddStoryFragmentArgs by navArgs()
    private var getFile: File? = null

    private val viewModel: PostStoryViewModel by viewModels()

    private var isImageGallery = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.image != null) {
            getFile = args.image
        }

        if (!isAllPermissionsGranted()) requestPermission()

        onView()

        setImagePreviewFromCamera()

        observePostStory()

        postStory()
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
                viewModel.postStory(description, imageFile)
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

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!isAllPermissionsGranted()) {
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
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun isAllPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToCameraFragment())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val TAG = AddStoryFragment::class.simpleName
    }
}