package com.example.play_test.ui.myBets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.play_test.BettingApplication
import com.example.play_test.databinding.FragmentMyBetsBinding
import kotlinx.coroutines.launch

class MyBetsFragment : Fragment() {

    private var _binding: FragmentMyBetsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val myBetsViewModel: MyBetsViewModel by viewModels {
        MyBetsViewModelFactory((activity?.application as BettingApplication).database.betHistoryDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMyBetsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myBetsViewModel.getBets().collect {
                    println("coletado $it")
                    if (it.isEmpty()) {
                        binding.ivEmptyHistory.visibility = View.VISIBLE
                        binding.tvHistory.visibility = View.VISIBLE
                    }

                    if (it.isNotEmpty()) {
                        binding.ivEmptyHistory.visibility = View.GONE
                        binding.tvHistory.visibility = View.GONE
                        binding.rvHistoryBets.visibility = View.VISIBLE
                        val adapter = MyBetsAdapter(it)
                        binding.rvHistoryBets.adapter = adapter
                        println("adapter")
                    }
                    binding.historyShrimmer.stopShimmer()
                    binding.historyShrimmer.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
