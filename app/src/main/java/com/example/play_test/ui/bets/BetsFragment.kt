package com.example.play_test.ui.bets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.play_test.data.pojo.CartItemPojo
import com.example.play_test.databinding.FragmentBetsBinding
import com.example.play_test.ui.currentBets.CurrentBetsViewModel
import com.example.play_test.ui.home.HomeViewModel

class BetsFragment : Fragment(), RecyclerBetsListener, RecyclerOddsListener {

    private lateinit var binding: FragmentBetsBinding
    private val betsViewModel: BetsViewModel by activityViewModels()
    private val currentBetsViewModel: CurrentBetsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        betsViewModel.selectedPredictions?.let { predictions ->
            binding.tvCountryName.text = predictions[0].competitionCluster
            val adapter = BetsAdapter(predictions, this@BetsFragment)
            binding.rvGames.adapter = adapter
        }
        binding.ivAddToCart.setOnClickListener {
            val odds = betsViewModel.selectedOdds
            odds.forEach {

                val cartItem = betsViewModel.mountCartItem(it.predictionResponse, it.odd)
                currentBetsViewModel.addCartItem(cartItem)
            }
        }
        listeners()
    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun addToCart(cartItemPojo: CartItemPojo) {
        currentBetsViewModel.addCartItem(cartItemPojo)
    }

    override fun addOdd(oddPojo: OddPojo) {
        betsViewModel.addOdd(oddPojo)
    }

    override fun removeOdd(oddPojo: OddPojo) {
        betsViewModel.removeOdd(oddPojo)
    }
}
