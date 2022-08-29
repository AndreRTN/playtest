package com.example.play_test.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.play_test.R
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.databinding.PredictionsItemBinding
import java.lang.Exception

data class PredictionsRecyclerList(
    val country: String,
    val items: List<PredictionResponse>
)

class PredictionsAdapter(private val list: List<PredictionsRecyclerList>, val listener: RecyclerPredictionsListener) :
    RecyclerView.Adapter<PredictionsAdapter.PredictionsViewHolder>() {

    inner class PredictionsViewHolder(private val binding: PredictionsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PredictionsRecyclerList) {
            val image = "https://countryflagsapi.com/png/${item.country}"

            Glide.with(binding.root).load(image).error(R.drawable.ic_baseline_flag_24).into(binding.ivFlag)

            binding.tvCountry.text = item.country
            binding.tvQuantity.text = item.items.size.toString()
            binding.root.setOnClickListener {
                listener.onClickPrediction(item.items)
            }
            binding.ivToGames.setOnClickListener {
                listener.onClickPrediction(item.items)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionsViewHolder {
        val binding =
            PredictionsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredictionsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PredictionsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
