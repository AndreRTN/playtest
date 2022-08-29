package com.example.play_test.ui.myBets

import DecimalUtils.toCurrency
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.play_test.R
import com.example.play_test.data.models.BetHistory
import com.example.play_test.databinding.HistoryBetItemBinding
import java.time.LocalDateTime

class MyBetsAdapter(val list: List<BetHistory>) :
    RecyclerView.Adapter<MyBetsAdapter.MyBetsViewHolder>() {

    inner class MyBetsViewHolder(private val binding: HistoryBetItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(betHistory: BetHistory) {
            val color = if (betHistory.win) ContextCompat.getColor(
                itemView.context,
                R.color.primary_green
            ) else ContextCompat.getColor(itemView.context, R.color.primary_red)

            binding.ivResult.backgroundTintList = ColorStateList.valueOf(color)
            binding.tvValue.setTextColor(color)
            binding.tvValue.text = betHistory.value.toCurrency()
            binding.tvOdd.text = "${betHistory.oddId} - ${betHistory.odd}"
            val dt: LocalDateTime = LocalDateTime.parse(betHistory.period)
            binding.tvDate.text = "${dt.dayOfMonth} ${dt.month.name}"
            binding.tvId.text = "Id: ${betHistory.uid}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBetsViewHolder {
        val binding =
            HistoryBetItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyBetsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyBetsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
