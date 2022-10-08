package com.example.biliardcompose.model

import androidx.annotation.DrawableRes
import com.example.biliardcompose.R

data class BiliardBoard(
    @DrawableRes val img : Int = R.drawable.spr_background4
) {
    companion object {
        val mockData : BiliardBoard = BiliardBoard()
    }
}
