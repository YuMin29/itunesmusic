package com.yumin.itunesmusic.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yumin.itunesmusic.data.Result
import com.yumin.itunesmusic.databinding.LayoutSearchItemBinding

class RecyclerViewAdapter(
    private val clickListener: OnItemClickListener,
    private val context: Context,
    private var searchResult: List<Result>
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding =
            LayoutSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchItemViewHolder(itemBinding, clickListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return searchResult.size
    }

    inner class SearchItemViewHolder(
        private val binding: LayoutSearchItemBinding,
        private val listener: OnItemClickListener
    ) : BaseViewHolder(binding.root), View.OnClickListener {
        override fun onBind(position: Int) {
            Glide.with(context).load(searchResult[position].artworkUrl100)
                .into(binding.musicImageView)
            binding.title.text = searchResult[position].trackName
            binding.artist.text = searchResult[position].artistName
            binding.root.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            listener.onItemClick(view, position)
        }
    }

    fun updateList(list: List<Result>) {
        searchResult = list
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}