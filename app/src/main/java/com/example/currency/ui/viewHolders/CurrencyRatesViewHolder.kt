package com.example.currency.ui.viewHolders

import android.content.Context
import com.example.currency.base.BaseViewHolder
import com.example.currency.databinding.LayoutCurrencyItemBinding
import com.example.domain.domain.models.CurrencyBean


class CurrencyRatesViewHolder(
    private val binding: LayoutCurrencyItemBinding,
    private val context: Context
) : BaseViewHolder<CurrencyBean.CurrencyRateBean>(binding) {
    override fun setItem(
        data: CurrencyBean.CurrencyRateBean?,
        itemClickListener: ((CurrencyBean.CurrencyRateBean) -> Unit)?
    ) {
        data?.let {
            with(binding) {
                binding.root.setOnClickListener {
                    itemClickListener?.invoke(data)
                }
                currencyCode.text = it.currencyCode
                currencyName.text = it.currencyRate.toString()
            }
        }
    }

}




