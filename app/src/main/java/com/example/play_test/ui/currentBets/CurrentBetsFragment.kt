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
import com.example.play_test.R
import com.example.play_test.databinding.FragmentCurrentBetsBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class CurrentBetsFragment : Fragment() {

    private var _binding: FragmentCurrentBetsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val fragmentsList = arrayListOf(CartsBet(), RunningBets())
    private val currentBetsViewModel: CurrentBetsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCurrentBetsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vpOption.adapter =
            CurrentBetsPagerAdapter(this, fragmentsList)
        binding.vpOption.isUserInputEnabled = false
        TabLayoutMediator(binding.tlOptions, binding.vpOption) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_baseline_shopping_cart_24)
                    tab.text = "Carrinho"
                }
                1 -> {
                    tab.text = "Em andamento"
                    tab.setIcon(R.drawable.ic_baseline_watch_later_24)
                }
                else -> {}
            }
        }.attach()

        listeners()
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentBetsViewModel.toRunningBets.collect {
                    if (it) {
                        binding.vpOption.setCurrentItem(1, false)
                        currentBetsViewModel.resetRunningBets()
                    }
                }
            }
        }
    }

    private fun listeners() {
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun toRunningBets() {
        binding.vpOption.setCurrentItem(1, true)
    }
}
