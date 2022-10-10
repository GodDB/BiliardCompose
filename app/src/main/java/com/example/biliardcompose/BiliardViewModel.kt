package com.example.biliardcompose

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.biliardcompose.exception.BiliardStateUpdateException
import com.example.biliardcompose.model.BiliardBall
import com.example.biliardcompose.model.BiliardBoard
import com.example.biliardcompose.model.BiliardStick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class BiliardViewModel(
    private val screenWidthPx: Float,
    private val screenHeightPx: Float
) : ViewModel() {

    private val _state: MutableStateFlow<BiliardState> = MutableStateFlow(BiliardState())
    val state: StateFlow<BiliardState> = _state.asStateFlow()

    private val updater: Updater = Updater()

    init {

    }

    private inline fun <reified T> updateSubState(block: (T) -> T) {
        _state.update {
            it.updateSubState(block)
        }
    }

    fun reset() {
        updater.stop()
        _state.value = BiliardState.mockData
    }

    fun onBiliardGesture(gesture: BiliardGesture) {
 /*       Log.e("godgod", "onBiliardGesture() -  $gesture")*/
        viewModelScope.launch {
            when (gesture) {
                is BiliardGesture.Board -> handleBoardGesture(gesture)
                is BiliardGesture.Button -> handleButtonGesture(gesture)
            }
        }
    }

    private suspend fun handleButtonGesture(gesture: BiliardGesture.Button) {
        when (gesture) {
            is BiliardGesture.Button.TouchUp -> {
                updater.stop()
                val totalScalar = _state.value.stick.scalar
                val degree = _state.value.stick.degree
                updateSubState<BiliardStick> {
                    it.copy(
                        scalar = 0,
                        x = it.x + (totalScalar * cos(Math.toRadians(it.degree.toDouble()))).toFloat(),
                        y = it.y + (totalScalar * sin(Math.toRadians(it.degree.toDouble()))).toFloat()
                    )
                }
                updateSubState<BiliardBall> {
                    it.copy(degree = degree)
                }
                updater.start(totalScalar * 3) { distance ->
                    var mainBall = _state.value.mainBall
                    val newX = mainBall.x + (distance * cos(Math.toRadians(mainBall.degree.toDouble()))).toFloat()
                    val newY = mainBall.y + (distance * sin(Math.toRadians(mainBall.degree.toDouble()))).toFloat()
                    val screenXBound = 50f..screenWidthPx - 50f
                    val screenYBound = 30f..screenHeightPx - 30f
                    if (!screenXBound.contains(newX) || !screenYBound.contains(newY)) {
                        Log.e("godgod", "${mainBall.degree}")
                        mainBall = mainBall.copy(degree = if(mainBall.degree >= 0) 180f - mainBall.degree else abs( mainBall.degree))
                    }

                    _state.update {
                        it.copy(
                            mainBall = mainBall.copy(
                                x = mainBall.x + (distance * cos(Math.toRadians(mainBall.degree.toDouble()))).toFloat(),
                                y = mainBall.y + (distance * sin(Math.toRadians(mainBall.degree.toDouble()))).toFloat()
                            )
                        )
                    }
                }
            }
            is BiliardGesture.Button.TouchDown -> {
                updater.start {
                    updateSubState<BiliardStick> {
                        it.copy(
                            scalar = it.scalar + 10,
                            x = it.x - (10 * cos(Math.toRadians(it.degree.toDouble()))).toFloat(),
                            y = it.y - (10 * sin(Math.toRadians(it.degree.toDouble()))).toFloat()
                        )
                    }
                }
            }
        }
    }

    private fun handleBoardGesture(gesture: BiliardGesture.Board) {
        when (gesture) {
            is BiliardGesture.Board.TouchDown -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        state = if (gesture.pointerCount == 1) BiliardStick.State.ROTATING else BiliardStick.State.GAZING,
                        x = if (gesture.pointerCount == 1) gesture.x - it.width / 2 else it.x,
                        y = if (gesture.pointerCount == 1) gesture.y - it.height / 2 else it.y
                    )
                }
            }

            is BiliardGesture.Board.TouchMove -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        state = if (gesture.pointerCount == 1) BiliardStick.State.ROTATING else BiliardStick.State.GAZING,
                        x = if (gesture.pointerCount == 1) gesture.x - it.width / 2 else it.x,
                        y = if (gesture.pointerCount == 1) gesture.y - it.height / 2 else it.y
                    )
                }
            }
            is BiliardGesture.Board.TouchUp -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        state = if (gesture.pointerCount == 1) BiliardStick.State.IDLE else BiliardStick.State.SHOOTING
                    )
                }
            }
            is BiliardGesture.Board.TouchCancel -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        state = if (gesture.pointerCount == 1) BiliardStick.State.IDLE else BiliardStick.State.SHOOTING
                    )
                }
            }

            is BiliardGesture.Board.Rotate -> {
                updateSubState<BiliardStick> {
                    it.copy(
                        state = BiliardStick.State.ROTATING,
                        degree = it.degree + gesture.degree
                    )
                }
            }
        }
    }

    companion object {
        fun createViewModelFactory(width: Float, height: Float): ViewModelProvider.Factory {
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
    val mainBall: BiliardBall = BiliardBall(1000f, 500f, BiliardBall.Type.WHITE)
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
            BiliardBall::class.java -> {
                this.copy(
                    mainBall = block(this.mainBall as T) as BiliardBall
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

    sealed class Board : BiliardGesture() {
        data class TouchDown(
            val x: Float,
            val y: Float,
            val pointerCount: Int
        ) : Board()

        data class TouchMove(
            val x: Float,
            val y: Float,
            val pointerCount: Int
        ) : Board()

        data class TouchUp(
            val x: Float,
            val y: Float,
            val pointerCount: Int
        ) : Board()

        data class TouchCancel(
            val x: Float,
            val y: Float,
            val pointerCount: Int
        ) : Board()

        data class Rotate(
            val degree: Float
        ) : Board()
    }

    sealed class Button : BiliardGesture() {
        object TouchDown : Button()
        object TouchUp : Button()
    }
}
