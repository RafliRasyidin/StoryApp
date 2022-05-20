package com.rasyidin.storyapp.presentation.component

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rasyidin.storyapp.databinding.DialogLogoutBinding

class DialogLogout : DialogFragment() {

    private var _binding: DialogLogoutBinding? = null
    private val binding: DialogLogoutBinding get() = _binding!!

    var onClickLogout: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLogoutBinding.inflate(inflater, container, false)
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
            btnLogout.setOnClickListener {
                onClickLogout?.invoke()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DialogLogout()

        val TAG = DialogLogout::class.simpleName
    }
}