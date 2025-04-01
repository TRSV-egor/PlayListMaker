package com.practicum.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.practicum.playlistmaker.media.ui.fragments.FavoriteTracksFragment
import com.practicum.playlistmaker.media.ui.fragments.PlaylistListFragment

class MediaViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> PlaylistListFragment.newInstance()
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistListFragment.newInstance()
        }
    }
}