package com.example.biliardcompose.model

import androidx.annotation.DrawableRes
import com.example.biliardcompose.R

data class BiliardBall(
    val x : Float,
    val y : Float,
    val type : Type,
    val degree : Float = 0f
) {

    enum class Type(@DrawableRes val img : Int) {
        WHITE(R.drawable.spr_ball2),
        YELLOW(R.drawable.spr_yellowball2),
        BLACK(R.drawable.spr_blackball2),
        RED(R.drawable.spr_redball2)
    }
}

