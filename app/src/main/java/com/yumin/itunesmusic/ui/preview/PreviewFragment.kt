package com.yumin.itunesmusic.ui.preview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.yumin.itunesmusic.R
import com.yumin.itunesmusic.databinding.FragmentPreviewBinding
import com.yumin.itunesmusic.repository.RemoteRepository
import com.yumin.itunesmusic.ui.MainViewModel
import com.yumin.itunesmusic.ui.ViewModelFactory

class PreviewFragment : Fragment() {

    companion object {
        fun newInstance() = PreviewFragment()
    }

    private lateinit var binding: FragmentPreviewBinding
    private lateinit var viewModel: MainViewModel
    lateinit var getContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreviewBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.previewAction.setOnClickListener { it ->
            if (it.isClickable) {
                if (!viewModel.startPreview) {
                    viewModel.startPreview()
                    binding.previewAction.setImageResource(R.drawable.ic_baseline_pause_circle_24)
                } else {
                    viewModel.pausePreview()
                    binding.previewAction.setImageResource(R.drawable.ic_baseline_play_circle_24)
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    viewModel.previewSeekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.skipNext.setOnClickListener {
            viewModel.skipNext()
        }

        binding.skipPrevious.setOnClickListener {
            viewModel.skipPrevious()
        }

        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(RemoteRepository())).get(
            MainViewModel::class.java
        )

        viewModel.previewResult.observe(viewLifecycleOwner, Observer {
            binding.previewArtist.text = it.artistName
            binding.previewTitle.text = it.trackName
            binding.albumName.text = it.collectionName
            Glide.with(getContext).load(it.artworkUrl100).into(binding.previewImage)
            viewModel.prepareMediaPlayer(it.previewUrl)
            binding.previewAction.setImageResource(R.drawable.ic_baseline_play_circle_24)
        })

        viewModel.canPreview.observe(viewLifecycleOwner, Observer {
            binding.previewAction.isClickable = it
        })

        viewModel.previewDuration.observe(viewLifecycleOwner, Observer {
            binding.seekBar.max = it / 1000
            binding.seekBar.progress = 0
        })

        viewModel.currentPosition.observe(viewLifecycleOwner, Observer {
            binding.seekBar.progress = it
            if (it < 10)
                binding.currentDurationText.text =
                    resources.getString(R.string.smaller_10_time_text, it)
            else
                binding.currentDurationText.text =
                    resources.getString(R.string.normal_time_text, it)

            val remain = binding.seekBar.max - it

            if (remain < 10)
                binding.remainDurationText.text =
                    resources.getString(R.string.smaller_10_time_text, remain)
            else
                binding.remainDurationText.text =
                    resources.getString(R.string.normal_time_text, remain)
        })

        viewModel.previewComplete.observe(viewLifecycleOwner, Observer {
            binding.previewAction.setImageResource(R.drawable.ic_baseline_play_circle_24)
            binding.seekBar.progress = 0
            binding.currentDurationText.text = resources.getString(R.string.smaller_10_time_text, 0)
            binding.remainDurationText.text =
                resources.getString(R.string.normal_time_text, binding.seekBar.max)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releaseMediaPlayer()
    }
}