package com.example.play_test.ui.bets

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.play_test.R
import com.example.play_test.databinding.OddItemBinding

data class OddPojo(
    val predictionId: String = "",
    val id: String,
    var checked: Boolean = false,
    val odd: Double
)

class OddsAdapter(val list: List<OddPojo>, val oddsListener: RecyclerOddsListener) :
    RecyclerView.Adapter<OddsAdapter.OddsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OddsViewHolder {
        val binding = OddItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OddsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OddsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class OddsViewHolder(val binding: OddItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OddPojo) {
            val context = binding.root.context
            binding.tvOddName.text = item.id
            binding.tvOddMultiply.text = item.odd.toString()
            binding.oddCard.setOnClickListener {
                list.filterNot { it.id == item.id }.forEachIndexed { index, odd ->
                    odd.checked = false
                }
                val checked = !item.checked
                if (checked) {
                    item.checked = true
                    oddsListener.addOdd(item)
                } else {
                    item.checked = false
                    oddsListener.removeOdd(item)
                }
                notifyItemRangeChanged(0, list.size)
            }
            if (item.checked) {
                binding.oddCard.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_purple))
                binding.tvOddMultiply.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.oddCard.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_gray))
                binding.tvOddMultiply.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.dark_purple
                    )
                )
            }
        }
    }
}
