package com.example.play_test.ui.home

import DecimalUtils.toCurrency
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.play_test.BettingApplication
import com.example.play_test.R
import com.example.play_test.data.api.APIClient
import com.example.play_test.data.api.ResultApi
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.enums.Federations
import com.example.play_test.data.repository.PredictionsRepository
import com.example.play_test.databinding.FragmentHomeBinding
import com.example.play_test.ui.bets.BetsViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), RecyclerPredictionsListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            (activity?.application as BettingApplication).database.balanceDao(),
            PredictionsRepository(APIClient.predictionsService())
        )
    }
    private val betsViewModel: BetsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (homeViewModel.currentFederation != 0) {
            binding.chipGroupFederations.check(homeViewModel.currentFederation)
        }

        binding.hsvChips.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    binding.hsvChips.scrollX = homeViewModel.chipScrollPosition.value
                    binding.hsvChips.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            })

        listeners()
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.money.collect {
                    binding.tvMoney.text = it.toCurrency()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            homeViewModel.predictions.collect { result ->
                when (result) {
                    is ResultApi.Loading -> {
                        binding.rvPredictions.visibility = View.GONE
                        binding.shrimmerPredictions.visibility = View.VISIBLE
                        binding.shrimmerPredictions.startShimmer()
                    }
                    is ResultApi.Success -> {

                        val recyclerList = homeViewModel.convertToRecyclerList()
                        val adapter = PredictionsAdapter(recyclerList, this@HomeFragment)
                        binding.rvPredictions.adapter = adapter
                        homeViewModel.updatePredictionList(result.data!!)
                        homeViewModel.plantListStateParcel?.let { parcelable ->
                            binding.rvPredictions.layoutManager?.onRestoreInstanceState(parcelable)
                            homeViewModel.plantListStateParcel = null
                        }

                        binding.shrimmerPredictions.visibility = View.GONE
                        binding.shrimmerPredictions.stopShimmer()
                        binding.rvPredictions.visibility = View.VISIBLE
                    }
                    is ResultApi.Error -> {
                        binding.shrimmerPredictions.visibility = View.GONE
                        binding.shrimmerPredictions.stopShimmer()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            homeViewModel.visibility.collect {
                if (it) changeImage(R.drawable.ic_visibility)
                else changeImage(R.drawable.ic_visibility_off)
            }
        }
    }

    private fun listeners() {
        binding.ivVisibility.setOnClickListener {
            homeViewModel.changeVisibility()
        }
        binding.chipGroupFederations.setOnCheckedChangeListener { group, checkedId ->
            val chipFederation = binding.chipGroupFederations.findViewById<Chip>(getCheckedChipId())
            val federations: Federations = Federations.valueOf(chipFederation.text.toString())
            homeViewModel.searchPredicitons(federations, checkedId)
        }
    }

    private fun changeImage(image: Int) {
        binding.ivVisibility.setImageResource(image)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.setChipScrollPosition(binding.hsvChips.scrollX)
        val listState = binding.rvPredictions.layoutManager?.onSaveInstanceState()
        listState?.let { homeViewModel.savePlanetListState(it) }
        _binding = null
    }

    private fun getCheckedChipId(): Int {
        return binding.chipGroupFederations.checkedChipId
    }

    override fun onClickPrediction(predictions: List<PredictionResponse>) {
        betsViewModel.selectPrediction(predictions)
        findNavController().navigate(R.id.to_games)
    }
}
