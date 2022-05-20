package com.rasyidin.storyapp.presentation.home

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.rasyidin.storyapp.R
import com.rasyidin.storyapp.databinding.FragmentDetailStoryBinding
import com.rasyidin.storyapp.presentation.component.FragmentBinding
import com.rasyidin.storyapp.presentation.component.withDateFormat

class DetailStoryFragment :
    FragmentBinding<FragmentDetailStoryBinding>(FragmentDetailStoryBinding::inflate) {

    private val args: DetailStoryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animation =
            TransitionInflater.from(requireActivity()).inflateTransition(android.R.transition.move)

        sharedElementEnterTransition = animation.setDuration(500L)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onAnimation()

        onView()
    }

    private fun onAnimation() {
        val story = args.story
        ViewCompat.setTransitionName(
            binding.imgStory,
            getString(R.string.img_story_transition).plus(story.id)
        )
        ViewCompat.setTransitionName(
            binding.tvName,
            getString(R.string.name_transition).plus(story.id)
        )
        ViewCompat.setTransitionName(
            binding.tvDescription,
            getString(R.string.description_transition).plus(story.id)
        )
        ViewCompat.setTransitionName(
            binding.tvDate,
            getString(R.string.date_transition).plus(story.id)
        )
    }

    private fun onView() {
        val story = args.story
        binding.apply {
            with(story) {
                Glide.with(requireActivity())
                    .load(photoUrl)
                    .into(imgStory)

                tvName.text = name
                tvDescription.text = description
                tvDate.text = createdAt?.withDateFormat()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}