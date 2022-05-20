package com.rasyidin.storyapp.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.data.model.Story
import com.rasyidin.storyapp.data.utils.ResultState
import com.rasyidin.storyapp.data.utils.isLoading
import com.rasyidin.storyapp.data.utils.onFailure
import com.rasyidin.storyapp.data.utils.onSuccess
import com.rasyidin.storyapp.databinding.FragmentHomeBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.component.StoryAdapter
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
        }
    }

    private fun observeStories() {
        lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stories.collect { result ->

                    handleLoading(result)

                    result.onSuccess { stories ->
                        storyAdapter.submitList(stories)
                        binding.rvStory.smoothScrollToPosition(0)
                    }

                    result.onFailure { message ->
                        Log.e("HomeFragment", message)
                    }
                }
            }
        }
    }

    private fun handleLoading(result: ResultState<List<Story>>) {
        binding.apply {
            if (isLoading(result)) {
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