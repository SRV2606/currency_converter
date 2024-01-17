package com.example.starwars.ui.viewHolders

import android.content.Context
import android.util.Log
import com.example.domain.domain.models.MatchBean
import com.example.starwars.base.BaseViewHolder
import com.example.starwars.databinding.ItemMatchesCardBinding


class MatchScreenItemViewHolder(
    private val binding: ItemMatchesCardBinding,
    private val context: Context
) : BaseViewHolder<MatchBean>(binding) {
    override fun setItem(
        data: MatchBean?,
        itemClickListener: ((MatchBean) -> Unit)?
    ) {
        Log.d("SHAW_TAG", "setItem: " + data)
        data?.let {
            with(binding) {
                it.cardColor?.let { it1 -> cardHolderCV.setCardBackgroundColor(it1) }
                player1NameTV.text = it.player1.id.toString()
                player2NameTV.text = it.player2.id.toString()
                player1PointTV.text = it.player1.score.toString()
                player2PointTV.text = it.player2.score.toString()
            }
        }
    }

}





