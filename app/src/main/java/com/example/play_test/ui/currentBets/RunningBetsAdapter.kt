package com.example.play_test.ui.currentBets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.play_test.databinding.RunningBetsItemBinding
import com.example.play_test.domain.entity.Bets
import java.time.LocalDateTime

class RunningBetsAdapter(val bets: MutableList<Bets>) :
    RecyclerView.Adapter<RunningBetsAdapter.RunningBetsViewHolder>() {

    inner class RunningBetsViewHolder(val binding: RunningBetsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(bet: Bets) {
            binding.tvAwayTeam.text = bet.awayTeam
            binding.tvHomeTeam.text = bet.homeTeam
            binding.tvAwayScore.text = bet.awayScore.toString()
            binding.tvHomeScore.text = bet.homeScore.toString()
            val milliseconds = bet.timeToEnd * 1000
            val minutes = milliseconds / 1000 / 60
            val seconds = milliseconds / 1000 % 60
            binding.tvTimer.text =
                "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            binding.tvOdd.text = "${bet.oddPojo.id} - ${bet.oddPojo.odd}"
            val dt: LocalDateTime = LocalDateTime.parse(bet.date)
            binding.tvDate.text =
                "${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padEnd(2, '0')}"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTimer() {
        notifyDataSetChanged()
    }

    fun removeItem(item: Bets) {
        if (bets.contains(item)) {
            val index = bets.indexOf(item)
            bets.remove(item)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunningBetsViewHolder {
        val binding =
            RunningBetsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunningBetsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bets.size
    }

    override fun onBindViewHolder(holder: RunningBetsViewHolder, position: Int) {
        holder.bind(bets[position])
    }
}
