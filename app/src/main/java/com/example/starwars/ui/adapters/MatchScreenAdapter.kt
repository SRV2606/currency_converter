package com.example.starwars.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.domain.domain.models.MatchBean
import com.example.starwars.base.BaseViewHolder
import com.example.starwars.databinding.ItemMatchesCardBinding
import com.example.starwars.ui.viewHolders.MatchScreenItemViewHolder

class MatchScreenAdapter(
    private val itemClickListener: (MatchBean) -> Unit,
    private val context: Context,
) : ListAdapter<MatchBean, BaseViewHolder<*>>(
    DIFF_CALLBACK
) {


    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<MatchBean>() {
                override fun areItemsTheSame(
                    oldItem: MatchBean,
                    newItem: MatchBean
                ): Boolean {
                    return oldItem.match == newItem.match
                }

                override fun areContentsTheSame(
                    oldItem: MatchBean,
                    newItem: MatchBean
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return MatchScreenItemViewHolder(
            ItemMatchesCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), context
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as MatchScreenItemViewHolder).setItem(
            getItem(position), itemClickListener
        )
    }


    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)

    }
}