package com.example.biliardcompose

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.biliardcompose.model.BiliardBall
import com.example.biliardcompose.model.BiliardBoard
import com.example.biliardcompose.model.BiliardStick

@Composable
fun BiliardScreen(viewModel: BiliardViewModel) {
    val biliardState by viewModel.state.collectAsState()
    BiliardUIScreen(
        state = biliardState,
        onGesture = viewModel::onBiliardGesture
    )
}

@Composable
private fun BiliardUIScreen(
    state: BiliardState,
    onGesture: (BiliardGesture) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BiliardBoardContent(
                board = state.board,
                onTouchDown = { x, y, pointerCount ->
                    onGesture.invoke(BiliardGesture.Board.TouchDown(x, y, pointerCount))
                },
                onTouchMove = { x, y, pointerCount ->
                    onGesture.invoke(BiliardGesture.Board.TouchMove(x, y, pointerCount))
                },
                onTouchUp = { x, y, pointerCount ->
                    onGesture.invoke(BiliardGesture.Board.TouchUp(x, y, pointerCount))
                },
                onTouchCancel = { x, y, pointerCount ->
                    onGesture.invoke(BiliardGesture.Board.TouchCancel(x, y, pointerCount))
                }
            )
            BiliardStickContent(stick = state.stick)
      /*      BiliardBallsContent(balls = state.balls)*/
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BiliardBoardContent(
    board: BiliardBoard,
    onTouchDown: (x: Float, y: Float, pointerCount: Int) -> Unit,
    onTouchMove: (x: Float, y: Float, pointerCount: Int) -> Unit,
    onTouchUp: (x: Float, y: Float, pointerCount: Int) -> Unit,
    onTouchCancel: (x: Float, y: Float, pointerCount: Int) -> Unit
) {
    val onTouchDownState = rememberUpdatedState(newValue = onTouchDown)
    val onTouchMoveState = rememberUpdatedState(newValue = onTouchMove)
    val onTouchUpState = rememberUpdatedState(newValue = onTouchUp)
    val onTouchCancelState = rememberUpdatedState(newValue = onTouchCancel)

    Image(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter {
                when (it.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        onTouchDownState.value.invoke(
                            it.rawX,
                            it.rawY,
                            it.pointerCount
                        )
                    }
                    MotionEvent.ACTION_MOVE -> {
                        onTouchMoveState.value.invoke(
                            it.rawX,
                            it.rawY,
                            it.pointerCount
                        )
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        onTouchCancelState.value.invoke(
                            it.rawX,
                            it.rawY,
                            it.pointerCount
                        )
                    }
                    MotionEvent.ACTION_UP -> {
                        onTouchUpState.value.invoke(
                            it.rawX,
                            it.rawY,
                            it.pointerCount
                        )
                    }
                }
                true
            },
        contentScale = ContentScale.FillBounds,
        painter = painterResource(id = board.img), contentDescription = "당구대"
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BiliardStickContent(
    stick: BiliardStick
) {
    if (stick.isVisible) {
        Image(
            modifier = Modifier
                .graphicsLayer {
                    translationX = stick.x
                    translationY = stick.y
                },
            painter = painterResource(id = stick.img),
            contentDescription = "당구큐대"
        )
    }
}

@Composable
private fun BiliardBallsContent(balls: List<BiliardBall>) {

}


/*@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BiliardComposeTheme {
        BiliardUIScreen(BiliardState.mockData)
    }
}*/
