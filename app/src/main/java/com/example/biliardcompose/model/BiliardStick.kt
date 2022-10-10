package com.example.biliardcompose.model

import androidx.annotation.DrawableRes
import com.example.biliardcompose.R

data class BiliardStick(
    val x: Float,
    val y: Float,
    val width: Int = 600,
    val height: Int = 20,
    val degree: Float,
    val state: BiliardStick.State,
    val scalar: Int = 0,
    @DrawableRes val img: Int = R.drawable.spr_stick
) {
    companion object {
        val mockData: BiliardStick = BiliardStick(
            x = 0f,
            y = 0f,
            degree = 0f,
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
