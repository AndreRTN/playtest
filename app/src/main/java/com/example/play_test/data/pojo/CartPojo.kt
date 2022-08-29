package com.example.play_test.data.pojo

import android.util.Log

data class CartPojo(
    val items: MutableList<CartItemPojo> = mutableListOf(),
    var total: Double = 0.0
)

fun CartPojo.addToCart(value: CartItemPojo) {
    val hasCart = items.find { it.id == value.id }

    if (hasCart == null) {
        items.add(value)
        total.plus(value.value)

    } else {
        items.remove(hasCart)
        val newCartValue = hasCart.copy(
            winTeam = value.winTeam,
            loseTeam = value.loseTeam,
            odd = value.odd,
            draw = value.draw
        )
        items.add(newCartValue)

    }
}

fun CartPojo.removeFromCart(value: CartItemPojo) {
    items.remove(value)
    total.minus(value.value)
}
