package com.example.play_test.ui.currentBets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.play_test.BettingApplication
import com.example.play_test.databinding.FragmentRunningBetsBinding
import com.example.play_test.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class RunningBets : Fragment() {

    private lateinit var binding: FragmentRunningBetsBinding

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val runningBetsViewModel: RunningBetsViewModel by activityViewModels {
        val dao = (activity?.application as BettingApplication).database.betHistoryDao()
        val balanceDao = (activity?.application as BettingApplication).database.balanceDao()
        RunningBetsViewModelFactory(balanceDao, dao, activity?.application as BettingApplication)
    }
    private lateinit var adapter: RunningBetsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRunningBetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listeners()
        adapter = RunningBetsAdapter(runningBetsViewModel.bets.value)
        observers()
    }

    private fun listeners() {
    }

    private fun observers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                runningBetsViewModel.bets.collect {
                    if (it.isNotEmpty()) {
                        adapter = RunningBetsAdapter(it)
                        binding.llEmptyRunningBets.visibility = View.GONE
                        binding.rvRunningBets.adapter = adapter
                        binding.rvRunningBets.visibility = View.VISIBLE
                    } else {
                        binding.llEmptyRunningBets.visibility = View.VISIBLE
                        binding.rvRunningBets.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                runningBetsViewModel.itemsUpdated.collect {
                    if (it > 0) {
                        adapter.updateTimer()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                runningBetsViewModel.itemRemoved.collect {

                    it?.let {

                        adapter.removeItem(it)
                        if (adapter.itemCount == 0) {
                            binding.llEmptyRunningBets.visibility = View.VISIBLE
                            binding.rvRunningBets.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        runningBetsViewModel.resetRemovedItems()
    }
}
