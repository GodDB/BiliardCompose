package com.example.biliardcompose

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.biliardcompose.model.BiliardBall
import com.example.biliardcompose.model.BiliardBoard
import com.example.biliardcompose.model.BiliardStick

@Composable
fun BiliardScreen(viewModel: BiliardViewModel) {
    val biliardState by viewModel.state.collectAsState()
    BiliardUIScreen(
        state = biliardState,
        onGesture = viewModel::onBiliardGesture,
        onClickReset = viewModel::reset
    )
}

@Composable
private fun BiliardUIScreen(
    state: BiliardState,
    onGesture: (BiliardGesture) -> Unit,
    onClickReset : () -> Unit
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
                },
                onRotateChanged = { degree ->
                    onGesture.invoke(BiliardGesture.Board.Rotate(degree))
                }
            )
            BiliardStickContent(stick = state.stick)
            BiliardBallContent(ball = state.mainBall)
            BiliardShootButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onTouchDown = {
                    onGesture.invoke(BiliardGesture.Button.TouchDown)
                },
                onTouchUp = {
                    onGesture.invoke(BiliardGesture.Button.TouchUp)
                }
            )
            BiliardResetButton(onClick = onClickReset)
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
    onTouchCancel: (x: Float, y: Float, pointerCount: Int) -> Unit,
    onRotateChanged: (degree: Float) -> Unit
) {
    val onTouchDownState = rememberUpdatedState(newValue = onTouchDown)
    val onTouchMoveState = rememberUpdatedState(newValue = onTouchMove)
    val onTouchUpState = rememberUpdatedState(newValue = onTouchUp)
    val onTouchCancelState = rememberUpdatedState(newValue = onTouchCancel)

    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        onRotateChanged.invoke(rotationChange)
    }

    Image(
        modifier = Modifier
            .fillMaxSize()
            .transformable(transformState)
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
    val width = with(LocalDensity.current) {
        stick.width.toDp()
    }
    val height = with(LocalDensity.current) {
        stick.height.toDp()
    }
    Image(
        modifier = Modifier
            .size(width, height)
            .graphicsLayer {
                translationX = stick.x
                translationY = stick.y
                rotationZ = stick.degree
            },
        painter = painterResource(id = stick.img),
        contentScale = ContentScale.FillBounds,
        contentDescription = "당구큐대"
    )
}

@Composable
private fun BiliardBallContent(ball: BiliardBall) {
    Image(
        modifier = Modifier.graphicsLayer {
            translationX = ball.x
            translationY = ball.y
        },
        painter = painterResource(id = ball.type.img),
        contentDescription = "당구공"
    )
}

@Composable
private fun BiliardShootButton(
    modifier: Modifier,
    onTouchDown: () -> Unit,
    onTouchUp: () -> Unit
) {
    val onTouchDownState = rememberUpdatedState(newValue = onTouchDown)
    val onTouchUpState = rememberUpdatedState(newValue = onTouchUp)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Blue)
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onPress = {
                        onTouchDownState.value.invoke()
                        awaitRelease()
                        onTouchUpState.value.invoke()
                    }
                )
            }
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            text = "슈팅"
        )
    }
}

@Composable
private fun BiliardResetButton(onClick : () -> Unit) {
    Button(onClick = onClick) {
        Text(text = "리셋")
    }
}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BiliardComposeTheme {
        BiliardUIScreen(BiliardState.mockData)
    }
}*/
