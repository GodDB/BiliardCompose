package com.example.biliardcompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biliardcompose.ui.theme.BiliardComposeTheme

@Composable
fun BiliardScreen(viewModel: BiliardViewModel) {
    viewModel
    BiliardUIScreen()
}

@Composable
private fun BiliardUIScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Text("Android")
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BiliardComposeTheme {
        BiliardUIScreen()
    }
}
