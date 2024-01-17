package com.example.starwars.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.starwars.base.BaseViewHolder
import com.example.starwars.databinding.ItemPlayerWithPointCardBinding
import com.example.starwars.ui.viewHolders.PointsTableViewHolder

class PointsTableAdapter(
    private val itemClickListener: (PlayerWithPointsBean) -> Unit,
    private val context: Context,
) : ListAdapter<PlayerWithPointsBean, BaseViewHolder<*>>(
    DIFF_CALLBACK
) {


    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<PlayerWithPointsBean>() {
                override fun areItemsTheSame(
                    oldItem: PlayerWithPointsBean,
                    newItem: PlayerWithPointsBean
                ): Boolean {
                    return oldItem.points == newItem.points
                }

                override fun areContentsTheSame(
                    oldItem: PlayerWithPointsBean,
                    newItem: PlayerWithPointsBean
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return PointsTableViewHolder(
            ItemPlayerWithPointCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), context
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as PointsTableViewHolder).setItem(
            getItem(position), itemClickListener
        )
    }


    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)

    }
}