package com.rasyidin.storyapp.presentation.component

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.rasyidin.storyapp.databinding.DialogSuccessBinding

class DialogSuccess : DialogFragment() {

    private var _binding: DialogSuccessBinding? = null
    private val binding get() = _binding!!

    var onClickOk: (() -> Unit)? = null

    var onDialogDismiss: ((Boolean) -> Unit)? = null

    private var isButtonClicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bgDialog = ColorDrawable(Color.TRANSPARENT)
        val insetDrawable = InsetDrawable(bgDialog, 48)
        dialog?.window?.setBackgroundDrawable(insetDrawable)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        isButtonClicked = false

        binding.btnOk.setOnClickListener {
            isButtonClicked = true
            onClickOk?.invoke()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDialogDismiss?.invoke(isButtonClicked)
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): DialogSuccess = DialogSuccess()

        val TAG = DialogSuccess::class.simpleName
    }
}