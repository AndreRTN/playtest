package com.example.play_test

import android.animation.ObjectAnimator
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.play_test.data.shared.NotificationUtils
import com.example.play_test.databinding.ActivityMainBinding
import com.example.play_test.ui.currentBets.CurrentBetsViewModel

import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: com.example.play_test.data.shared.Preferences
    private lateinit var binding: ActivityMainBinding
    private val currentBetsViewModel: CurrentBetsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            true
        }

        super.onCreate(savedInstanceState)
        NotificationUtils.createChannel(this)
        preferences = com.example.play_test.data.shared.Preferences(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observers()
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setupWithNavController(navController)

        splashScreen.setOnExitAnimationListener { splashView ->
            val slideUp: ObjectAnimator =
                ObjectAnimator.ofFloat(
                    splashView.view,
                    View.TRANSLATION_Y,
                    0f,
                    -splashView.view.height.toFloat()
                )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 400
            slideUp.doOnEnd {
                splashView.remove()
            }
            slideUp.start()
        }

        lifecycleScope.launch {
            val seenOnboard = preferences.getOnboard().first()
            splashScreen.setKeepOnScreenCondition { false }
            if (seenOnboard) {
                navController.graph.setStartDestination(R.id.navigation_home)
                navController.popBackStack()
                navController.navigate(R.id.navigation_home)
            }
        }
    }

    private fun observers() {

        lifecycleScope.launch {
            currentBetsViewModel.quantity.collect {
                if (it > 0) {

                    binding.navView.getOrCreateBadge(R.id.navigation_current_bets).number =
                        it
                } else {
                    binding.navView.removeBadge(R.id.navigation_current_bets)
                }
            }
        }
    }
}
