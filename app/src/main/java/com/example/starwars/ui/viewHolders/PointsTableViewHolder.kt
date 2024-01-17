package com.example.starwars.ui.viewHolders

import android.content.Context
import com.bumptech.glide.Glide
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.starwars.base.BaseViewHolder
import com.example.starwars.databinding.ItemPlayerWithPointCardBinding


class PointsTableViewHolder(
    private val binding: ItemPlayerWithPointCardBinding,
    private val context: Context
) : BaseViewHolder<PlayerWithPointsBean>(binding) {
    override fun setItem(
        data: PlayerWithPointsBean?,
        itemClickListener: ((PlayerWithPointsBean) -> Unit)?
    ) {
        data?.let {
            with(binding) {
                binding.root.setOnClickListener {
                    itemClickListener?.invoke(data)
                }
                Glide.with(context).load(it.player.icon).into(playerImageIV)
                playerNameTV.text = it.player.name
                pointsTV.text = it.points.toString()
            }
        }
    }

}





