package com.example.play_test.ui.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.play_test.R
import com.example.play_test.databinding.FragmentOnboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class OnboardFragment : Fragment() {


    private lateinit var binding: FragmentOnboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigation.visibility = View.GONE
        val onboard: Onboard = arguments?.get("onboard") as Onboard
        onboard.let {
            Glide.with(this).load(it.image).into(binding.ivOnboard)
            binding.tvOnboardTitle.text = it.title
            binding.tvOnboardDescription.text = it.message
        }
    }

    override fun onDestroyView() {
        val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigation.visibility = View.VISIBLE
        super.onDestroyView()
    }
}