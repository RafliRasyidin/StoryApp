package com.rasyidin.storyapp.presentation.component

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rasyidin.storyapp.databinding.DialogAddLocationBinding

class DialogAskPostWithLocation: DialogFragment() {

    private var _binding: DialogAddLocationBinding? = null
    private val binding: DialogAddLocationBinding get() = _binding!!

    var onClickYes: (() -> Unit)? = null

    var onClickCancel: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bgDialog = ColorDrawable(Color.TRANSPARENT)
        val insetDrawable = InsetDrawable(bgDialog, 48)
        dialog?.window?.setBackgroundDrawable(insetDrawable)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        onView()
    }

    private fun onView() {
        binding.apply {
            btnYes.setOnClickListener {
                onClickYes?.invoke()
                dismiss()
            }

            btnNo.setOnClickListener {
                onClickCancel?.invoke()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = DialogAskPostWithLocation()

        val TAG = DialogAskPostWithLocation::class.simpleName
    }
}