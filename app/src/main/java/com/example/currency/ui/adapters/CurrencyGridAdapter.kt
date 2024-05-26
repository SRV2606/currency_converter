package com.example.currency.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.currency.base.BaseViewHolder
import com.example.currency.databinding.LayoutCurrencyItemBinding
import com.example.currency.ui.viewHolders.CurrencyRatesViewHolder
import com.example.domain.domain.models.CurrencyBean


class CurrencyGridAdapter(
    private val itemClickListener: (CurrencyBean.CurrencyRateBean) -> Unit,
    private val context: Context,
) : ListAdapter<CurrencyBean.CurrencyRateBean, BaseViewHolder<*>>(
    DIFF_CALLBACK
) {


    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CurrencyBean.CurrencyRateBean>() {
                override fun areItemsTheSame(
                    oldItem: CurrencyBean.CurrencyRateBean,
                    newItem: CurrencyBean.CurrencyRateBean
                ): Boolean {
                    return oldItem.currencyCode == newItem.currencyCode
                }

                override fun areContentsTheSame(
                    oldItem: CurrencyBean.CurrencyRateBean,
                    newItem: CurrencyBean.CurrencyRateBean
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return CurrencyRatesViewHolder(
            LayoutCurrencyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), context
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        (holder as CurrencyRatesViewHolder).setItem(
            getItem(position), itemClickListener
        )
    }


    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)

    }

    override fun getCurrentList(): MutableList<CurrencyBean.CurrencyRateBean> {
        return super.getCurrentList()
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}