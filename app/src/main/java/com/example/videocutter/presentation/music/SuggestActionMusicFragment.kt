package com.example.videocutter.presentation.music

import com.example.baseapp.base.extension.setOnSafeClick
import com.example.videocutter.R
import com.example.videocutter.common.srceen.VideoCutterFragment
import com.example.videocutter.databinding.SuggestActionMusicFragmentBinding

class SuggestActionMusicFragment: VideoCutterFragment<SuggestActionMusicFragmentBinding>(R.layout.suggest_action_music_fragment) {
    override fun onInitView() {
        super.onInitView()
        binding.tvSuggestActionMusicClose.setOnSafeClick {
            onBackPressedFragment()
        }
        binding.flSuggestActionMusicRoot.setOnSafeClick {
            onBackPressedFragment()
        }
    }

    override fun onBackPressedFragment() {
        parentFragmentManager.popBackStack()
    }
}
