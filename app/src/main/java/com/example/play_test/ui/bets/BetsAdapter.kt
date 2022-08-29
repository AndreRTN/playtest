package com.example.play_test.ui.bets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.dto.toPojo
import com.example.play_test.data.pojo.CartItemPojo
import com.example.play_test.databinding.GameItemBinding
import java.time.LocalDateTime
import java.util.*

class BetsAdapter(val list: List<PredictionResponse>, val oddListener: RecyclerOddsListener) :
    RecyclerView.Adapter<BetsAdapter.BetsViewHolder>() {

    inner class BetsViewHolder(private val binding: GameItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(item: PredictionResponse) {

            val dt: LocalDateTime = LocalDateTime.parse(item.lastUpdateAt)

            binding.tvAwayTeam.text = item.awayTeam
            binding.tvHomeTeam.text = item.homeTeam
            binding.tvTimestamp.text = "${dt.hour}:${dt.minute}, ${dt.dayOfMonth} ${dt.month.name}"
            binding.tvLeague.text = item.competitionName
            val adapter = OddsAdapter(item.odds.toPojo(item.id.toString()), oddListener)
            binding.rvOptions.adapter = adapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetsViewHolder {
        val binding = GameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BetsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BetsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
