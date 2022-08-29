package com.example.play_test.ui.currentBets

import DecimalUtils.toCurrency
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.play_test.BettingApplication
import com.example.play_test.R
import com.example.play_test.databinding.FragmentCartsBetBinding
import com.example.play_test.ui.bets.BetsViewModel
import com.example.play_test.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class CartsBet() : Fragment(), RecyclerCartsBetListener {

    private lateinit var binding: FragmentCartsBetBinding

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val currentBetsViewModel: CurrentBetsViewModel by activityViewModels()
    private val runningBetsViewModel: RunningBetsViewModel by activityViewModels {
        val dao = (activity?.application as BettingApplication).database.betHistoryDao()
        val balanceDao = (activity?.application as BettingApplication).database.balanceDao()
        RunningBetsViewModelFactory(balanceDao, dao, activity?.application as BettingApplication)
    }
    private val betsViewModel: BetsViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartsBetBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (currentBetsViewModel.cart.value.items.isNotEmpty()) {
            binding.rvCartBets.adapter =
                CartsBetAdapter(list = currentBetsViewModel.cart.value.items, this)
            binding.rvCartBets.visibility = View.VISIBLE
            binding.ivEmptyCart.visibility = View.GONE
            binding.tvEmptyCart.visibility = View.GONE
        }
        observers()
        currentBetsViewModel.totalCart()
        currentBetsViewModel.totalCost()
        binding.rvCartBets.setOnTouchListener { v, event ->

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v?.windowToken, 0)
            false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentBetsViewModel
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentBetsViewModel.totalValueWin.collect {

                    if (it == 0.0) {
                        binding.tvTotalWin.text = ""
                        validBets(false)
                    } else binding.tvTotalWin.text = "Ganhos: ${it.toCurrency()}"
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentBetsViewModel.totalCustValue.collect {
                    validBets(it > 0.0 && it <= homeViewModel.money.value)
                    binding.tvCust.text = it.toCurrency()
                    binding.tvBalance.text =
                        "Novo Saldo: ${(homeViewModel.money.value - it).toCurrency()}"
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    currentBetsViewModel.isCartValid.collect {
                        validBets(it)
                    }
                }
            }
        }
    }

    private fun validBets(value: Boolean) {
        if (value) {
            binding.tvBalance.visibility = View.VISIBLE
            binding.tvBalance.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_green
                )
            )
            binding.viewBet.visibility = View.VISIBLE
            binding.tvBetCenter.visibility = View.VISIBLE
            binding.tvCust.visibility = View.VISIBLE
            binding.tvTotalWin.visibility = View.VISIBLE
            binding.viewBet.setOnClickListener {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                val listBets = currentBetsViewModel.mountListBets()
                homeViewModel.subtractMoney(currentBetsViewModel.totalCustValue.value)
                currentBetsViewModel.clearList()
                betsViewModel.clearList()
                val adapter = binding.rvCartBets.adapter as CartsBetAdapter
                runningBetsViewModel.initializeBets(listBets)
                currentBetsViewModel.inRunningBets()
                adapter.clear()
                validBets(false)
                showEmptyCart()
            }
        } else {
            binding.tvBalance.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_red
                )
            )
            binding.tvBalance.visibility = View.GONE
            binding.viewBet.visibility = View.GONE

            binding.tvBetCenter.visibility = View.GONE
            binding.tvCust.visibility = View.GONE
            binding.tvTotalWin.visibility = View.GONE
            binding.viewBet.setOnClickListener { }
        }
    }

    override fun onRemove(position: Int) {

        betsViewModel.removeOddFromPosition(position)
        if (currentBetsViewModel.removeCartItem(position)) {
            showEmptyCart()
        }
    }

    private fun showEmptyCart() {
        binding.tvEmptyCart.visibility = View.VISIBLE
        binding.ivEmptyCart.visibility = View.VISIBLE
        binding.rvCartBets.visibility = View.GONE
    }

    override fun validatePrice(position: Int, price: String): Boolean {
        return currentBetsViewModel.validatePrice(position, price, homeViewModel.money.value)
    }

    override fun isInitialPriceValid(price: Double): Boolean {
        return currentBetsViewModel.validateInitialPrice(price, homeViewModel.money.value)
    }
}
