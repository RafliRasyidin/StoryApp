package com.rasyidin.storyapp.presentation.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rasyidin.storyapp.data.model.ITEM_SETTING
import com.rasyidin.storyapp.databinding.FragmentSettingBinding
import com.rasyidin.storyapp.presentation.component.DialogLogout
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.component.SettingAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : FragmentBinding<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val viewModel: SettingViewModel by activityViewModels()

    private val settingAdapter: SettingAdapter by lazy {
        SettingAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onView()

        setupAdapter()

    }

    private fun setupAdapter() {
        binding.rvSetting.apply {
            adapter = settingAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )
            settingAdapter.submitList(ITEM_SETTING)
        }
    }

    private fun onView() {
        binding.apply {
            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        settingAdapter.onItemClick = { setting ->
            when (setting.id) {
                1 -> {
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(intent)
                }
                2 -> {
                    showDialogLogout()
                }
            }
        }

    }

    private fun showDialogLogout() {
        if (childFragmentManager.findFragmentByTag(DialogLogout.TAG ) == null) {
            val dialog = DialogLogout.newInstance()
            dialog.showNow(childFragmentManager, DialogLogout.TAG)
            dialog.onClickLogout = {
                viewModel.logout()
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToAuthActivity())
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}