package com.rasyidin.storyapp.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.cachedIn
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.databinding.FragmentHomeBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.component.StoryAdapter
import com.rasyidin.storyapp.presentation.component.withDateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : FragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by activityViewModels()

    private val storyAdapter: StoryAdapter by lazy { StoryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getStories()

        onView()

        setupAdapter()

        observeStories()

    }

    private fun onView() {
        binding.apply {
            fabAddStory.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddStoryFragment())
            }

            imgSetting.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingFragment())
            }

            storyAdapter.onItemClick = { story, imgStory, tvName, tvDescription, tvDate ->
                ViewCompat.setTransitionName(
                    imgStory,
                    getString(R.string.img_story_transition)
                )
                ViewCompat.setTransitionName(
                    tvName,
                    getString(R.string.name_transition)
                )
                ViewCompat.setTransitionName(
                    tvDescription,
                    getString(R.string.description_transition)
                )
                ViewCompat.setTransitionName(
                    tvDate,
                    getString(R.string.date_transition)
                )
                val extras = FragmentNavigatorExtras(
                    imgStory to getString(R.string.img_story_transition).plus(story.id),
                    tvName to getString(R.string.name_transition).plus(story.id),
                    tvDescription to getString(R.string.description_transition).plus(story.id),
                    tvDate to getString(R.string.date_transition).plus(story.id),
                )

                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(story),
                    extras
                )
            }

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
                viewModel.getStories()
            }

            imgMap.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStoryByLocationMapFragment())
            }
        }
    }

    private fun observeStories() {
        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagingStories.cachedIn(this).collect { result ->
                    storyAdapter.submitData(result)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { isLoading ->
                    handleLoading(isLoading)
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val date = "2011-11-07"
        date.withDateFormat()
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                shimmerStory.visibility = View.VISIBLE
                rvStory.visibility = View.GONE
            } else {
                shimmerStory.visibility = View.GONE
                rvStory.visibility = View.VISIBLE
            }
        }
    }

    private fun setupAdapter() {
        binding.rvStory.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(requireActivity())

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        binding.fabAddStory.show()
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.fabAddStory.isShown) {
                        binding.fabAddStory.hide()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}