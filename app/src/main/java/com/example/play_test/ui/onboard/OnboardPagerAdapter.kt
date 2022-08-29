package com.example.play_test.ui.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.play_test.R
import java.io.Serializable

data class Onboard(
    val title: String,
    val message: String,
    val image: Int
) : Serializable


private val onboardList = listOf(
    Onboard(
        title = "Apostas de um jeito fácil",
        message = "Aposte no seu time de coração com 1 clique e ganhe o seu prêmio.",
        image = R.drawable.onboard_1
    ),
    Onboard(
        title = "Múltilas Apostas",
        message = "Faça várias apostas para multiplicar ainda mais seus ganhos!",
        image = R.drawable.onboard_3
    ),
    Onboard(
        title = "Apostas Customizadas",
        message = "Não sabe em qual time apostar? não tem problema, você pode arriscar qual jogador vai marcar, quantidade de gols, entre outras opções.",
        image = R.drawable.onboard_2
    ),
)

class OnboardPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return onboardList.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = OnboardFragment()
        val bundle = Bundle()
        bundle.putSerializable("onboard", onboardList[position])
        fragment.arguments = bundle
        return fragment
    }
}