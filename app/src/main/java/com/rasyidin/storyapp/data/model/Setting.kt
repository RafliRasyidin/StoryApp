package com.rasyidin.storyapp.data.model

import com.rasyidin.storyapp.R

data class Setting(
    val id: Int,
    val image: Int,
    val title: Int,
)

val ITEM_SETTING = arrayListOf(
    Setting(
        1,
        R.drawable.ic_language,
        R.string.language
    ),
    Setting(
        2,
        R.drawable.ic_logout,
        R.string.logout
    )
)
