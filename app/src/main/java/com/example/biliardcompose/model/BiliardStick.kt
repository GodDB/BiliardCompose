package com.example.biliardcompose.model

import androidx.annotation.DrawableRes
import com.example.biliardcompose.R

data class BiliardStick(
    val x : Float,
    val y : Float,
    val degree : Float,
    val isVisible : Boolean,
    val state : BiliardStick.State,
    @DrawableRes val img : Int = R.drawable.spr_stick
) {
    companion object {
        val mockData : BiliardStick = BiliardStick(
            x = 0f,
            y = 0f,
            degree = 0f,
            isVisible = false,
            state = State.IDLE
        )
    }

    enum class State {
        IDLE,
        ROTATING,
        GAZING,
        SHOOTING
    }
}
