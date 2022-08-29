package com.example.play_test.ui.currentBets

import DecimalUtils.fromCurrency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.play_test.data.pojo.CartItemPojo
import com.example.play_test.data.pojo.CartPojo
import com.example.play_test.data.pojo.addToCart
import com.example.play_test.data.pojo.removeFromCart
import com.example.play_test.domain.entity.Bets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CurrentBetsViewModel : ViewModel() {

    private var _cart: MutableStateFlow<CartPojo> = MutableStateFlow(CartPojo())
    val cart: StateFlow<CartPojo> get() = _cart

    val toRunningBets: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val quantity: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalValueWin: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val isCartValid: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val totalCustValue: MutableStateFlow<Double> = MutableStateFlow(0.0)
    fun addCartItem(cartItem: CartItemPojo) {
        val newCart = _cart.value.copy()
        newCart.addToCart(cartItem)
        _cart.value = newCart
        viewModelScope.launch {
            quantity.emit(newCart.items.size)
        }
    }

    fun removeCartItem(position: Int): Boolean {
        val newCart = _cart.value.copy()
        newCart.removeFromCart(newCart.items[position])
        viewModelScope.launch {
            _cart.emit(newCart)
            quantity.emit(newCart.items.size)
            totalCart()
            totalCost()
        }
        return newCart.items.isEmpty()
    }

    fun validateInitialPrice(price: Double, totalValue: Double): Boolean {
        return price <= totalValue
    }

    fun validatePrice(position: Int, price: String, totalValue: Double): Boolean {
        val findItem = _cart.value.items[position]
        val newItem = findItem.copy(value = price.fromCurrency())
        val copyCart = _cart.value.copy()
        copyCart.items[position] = newItem
        copyCart.total = copyCart.items.sumOf { it.value * it.odd.odd }
        _cart.value = copyCart
        totalCart()
        totalCost()
        return if (price.fromCurrency() > totalValue) {
            viewModelScope.launch { totalValueWin.emit(0.0) }
            false
        } else {
            true
        }
    }

    fun totalCost() {
        val cost = _cart.value.items.sumOf { it.value }

        viewModelScope.launch {
            totalCustValue.emit(cost)
        }
    }

    fun totalCart() {

        viewModelScope.launch {
            totalValueWin.emit(_cart.value.items.sumOf { it.value * it.odd.odd })
        }
    }

    fun mountListBets(): List<Bets> {

        return _cart.value.items.filter { it.value != 0.0 }.map { mountBets(it) }
    }

    fun clearList() {
        viewModelScope.launch {
            _cart.emit(CartPojo())
            quantity.emit(0)
        }
    }

    fun inRunningBets() {
        viewModelScope.launch {
            toRunningBets.emit(true)
        }
    }

    fun resetRunningBets() {
        viewModelScope.launch {
            toRunningBets.emit(false)
        }
    }

    private fun mountBets(cartItem: CartItemPojo): Bets {

        return Bets(
            value = cartItem.value,
            oddPojo = cartItem.odd,
            id = cartItem.id,
            awayTeam = cartItem.awayTeam,
            homeTeam = cartItem.homeTeam,
            timeToEnd = Random.nextInt(40, 80),
            draw = cartItem.draw,
            date = cartItem.date,
            winTeam = cartItem.winTeam,
            loseTeam = cartItem.loseTeam
        )
    }
}
