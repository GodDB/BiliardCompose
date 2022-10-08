package com.example.biliardcompose

import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.biliardcompose.exception.BiliardStateUpdateException
import com.example.biliardcompose.model.BiliardBoard
import com.example.biliardcompose.model.BiliardStick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BiliardViewModel(
    private val screenWidthDp: Dp,
    private val screenHeightDp: Dp
) : ViewModel() {

    private val _state: MutableStateFlow<BiliardState> = MutableStateFlow(BiliardState())
    val state: StateFlow<BiliardState> = _state.asStateFlow()

    init {
        Log.e("godgod", "$screenWidthDp  $screenHeightDp")
    }

    private inline fun <reified T> updateSubState(block: (T) -> T) {
        _state.update {
            it.updateSubState(block)
        }
    }

    fun onBiliardGesture(gesture: BiliardGesture) {
        Log.e("godgod", "onBiliardGesture() -  $gesture")
        when (gesture) {
            is BiliardGesture.Board -> handleBoardGesture(gesture)
        }


    }

    private fun handleBoardGesture(gesture: BiliardGesture.Board) {
        if (gesture.pointerCount > 2) return
        when (gesture) {
            is BiliardGesture.Board.TouchDown -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        isVisible = true,
                        state = if (gesture.pointerCount == 1) BiliardStick.State.ROTATING else BiliardStick.State.GAZING
                    )
                }
            }

            is BiliardGesture.Board.TouchMove -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        isVisible = true,
                        state = if (gesture.pointerCount == 1) BiliardStick.State.ROTATING else BiliardStick.State.GAZING,
                        x = if(gesture.pointerCount == 1) gesture.x else it.x,
                        y =if(gesture.pointerCount == 1) gesture.y else it.y
                    )
                }
            }
            is BiliardGesture.Board.TouchUp -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        isVisible = gesture.pointerCount != 1,
                        state = if (gesture.pointerCount == 1) BiliardStick.State.IDLE else BiliardStick.State.SHOOTING
                    )
                }
            }
            is BiliardGesture.Board.TouchCancel -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        isVisible = gesture.pointerCount != 1,
                        state = if (gesture.pointerCount == 1) BiliardStick.State.IDLE else BiliardStick.State.SHOOTING
                    )
                }
            }
        }
    }

    companion object {
        fun createViewModelFactory(width: Dp, height: Dp): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return BiliardViewModel(width, height) as T
                }
            }
        }
    }
}

data class BiliardState(
    val board: BiliardBoard = BiliardBoard.mockData,
    val stick: BiliardStick = BiliardStick.mockData,
//    val balls: List<BiliardBall> = listOf()
) {

    inline fun <reified T> updateSubState(block: (T) -> T): BiliardState {
        return when (T::class.java) {
            BiliardBoard::class.java -> {
                this.copy(
                    board = block(this.board as T) as BiliardBoard
                )
            }
            BiliardStick::class.java -> {
                this.copy(
                    stick = block(this.stick as T) as BiliardStick
                )
            }
            else -> {
                throw BiliardStateUpdateException()
            }
        }
    }

    companion object {
        val mockData = BiliardState(
            board = BiliardBoard.mockData,
            stick = BiliardStick.mockData,
            /*   balls = listOf()*/
        )
    }
}

sealed class BiliardGesture {
    sealed class Board(
        open val x: Float,
        open val y: Float,
        open val pointerCount: Int
    ) : BiliardGesture() {
        data class TouchDown(
            override val x: Float,
            override val y: Float,
            override val pointerCount: Int
        ) : Board(x, y, pointerCount)

        data class TouchMove(
            override val x: Float,
            override val y: Float,
            override val pointerCount: Int
        ) : Board(x, y, pointerCount)

        data class TouchUp(
            override val x: Float,
            override val y: Float,
            override val pointerCount: Int
        ) : Board(x, y, pointerCount)

        data class TouchCancel(
            override val x: Float,
            override val y: Float,
            override val pointerCount: Int
        ) : Board(x, y, pointerCount)
    }
}
