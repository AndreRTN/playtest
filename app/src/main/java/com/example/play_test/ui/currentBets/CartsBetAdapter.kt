package com.example.play_test.ui.currentBets

import DecimalUtils.fromCurrency
import DecimalUtils.toCurrency
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.play_test.data.pojo.CartItemPojo
import com.example.play_test.data.shared.CurrencyTextWatcher
import com.example.play_test.databinding.CartBetItemBinding

class CartsBetAdapter(val list: MutableList<CartItemPojo>, val listener: RecyclerCartsBetListener) :
    RecyclerView.Adapter<CartsBetAdapter.CartsBetViewHolder>() {
    inner class CartsBetViewHolder(val binding: CartBetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: CartItemPojo) {
            binding.ivClose.setOnClickListener {
                listener.onRemove(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            binding.tvOddMultiply.text = item.odd.odd.toString()
            binding.tvWinningsValues.text = (item.odd.odd * item.value).toCurrency()
            binding.tvTeams.text = "${item.homeTeam} vs ${item.awayTeam}"
            if (item.draw) {
                binding.tvToResult.text = "Empate"
            } else {
                binding.tvToResult.text = "Vitória - "
                binding.tvTeam.text = item.winTeam
            }
            val initialPriceValid = listener.isInitialPriceValid(item.value)
            if (!initialPriceValid) {
                binding.tvWinningsValues.text = 0.0.toCurrency()
                binding.tilBetValue.error = "Valor maior que o saldo atual"
            } else {

            }

            binding.tilBetValue.editText?.run {
                setText(item.value.toCurrency())
                addTextChangedListener(CurrencyTextWatcher(this))
                doAfterTextChanged {
                    val isValid = listener.validatePrice(adapterPosition, it.toString())
                    if (!isValid) {
                        binding.tvWinningsValues.text = 0.0.toCurrency()
                        binding.tilBetValue.error = "Valor maior que o saldo atual"
                    } else {
                        binding.tvWinningsValues.text =
                            (item.odd.odd * it.toString().fromCurrency()).toCurrency()
                        binding.tilBetValue.error = null
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartsBetViewHolder {
        val binding = CartBetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartsBetViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CartsBetViewHolder, position: Int) {
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }
}
