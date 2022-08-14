package com.yumin.itunesmusic.ui.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yumin.itunesmusic.R
import com.yumin.itunesmusic.databinding.FragmentMainBinding
import com.yumin.itunesmusic.repository.RemoteRepository
import com.yumin.itunesmusic.ui.MainViewModel
import com.yumin.itunesmusic.ui.ViewModelFactory
import com.yumin.itunesmusic.ui.preview.PreviewFragment

class MainFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var getContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getContext = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(RemoteRepository())).get(
            MainViewModel::class.java
        )
        adapter = RecyclerViewAdapter(this, getContext, emptyList())
        binding.resultView.adapter = adapter
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchResult(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        viewModel.searchResult.observe(viewLifecycleOwner, Observer {
            if (it.results.isEmpty()) {
                // show empty view
                binding.resultView.visibility = View.GONE
                binding.emptyView.root.visibility = View.VISIBLE
            } else {
                // update recycler view
                Log.d("MainFragment", "result size ${it.results.size}")
                adapter.updateList(it.results)
                binding.resultView.visibility = View.VISIBLE
                binding.emptyView.root.visibility = View.GONE
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })
    }

    override fun onItemClick(view: View, position: Int) {
        requireActivity().supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container, PreviewFragment.newInstance())
            addToBackStack("MainFragment")
        }
        viewModel.setPreviewResult(position)
    }
}