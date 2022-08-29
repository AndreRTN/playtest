package com.example.play_test.ui.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.play_test.R
import com.example.play_test.data.shared.Preferences
import com.example.play_test.databinding.FragmentOnboardMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class OnboardMain : Fragment() {


    private lateinit var binding: FragmentOnboardMainBinding
    private lateinit var preferences: Preferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = OnboardPagerAdapter(this)
        preferences = Preferences(requireContext())
        binding.vpOnboard.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.vpOnboard) { tab, position ->
            //Some implementation
        }.attach()
        listeners()
    }

    private fun listeners() {
        binding.btnOnboardNext.setOnClickListener {
            binding.vpOnboard.currentItem = binding.vpOnboard.currentItem + 1
            if(binding.vpOnboard.currentItem == binding.vpOnboard.adapter!!.itemCount - 1){
                binding.btnOnboardNext.text = getString(R.string.onboard_done)
                binding.vpOnboard.isUserInputEnabled = false
                binding.btnOnboardNext.setOnClickListener {
                    lifecycleScope.launch {
                        preferences.saveOnboard()
                        findNavController().graph.setStartDestination(R.id.navigation_home)
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.navigation_home)
                    }

                }
            }
        }
    }
}