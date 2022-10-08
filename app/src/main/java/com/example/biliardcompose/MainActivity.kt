package com.example.biliardcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biliardcompose.ui.theme.BiliardComposeTheme
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiliardComposeTheme {
                val width = LocalConfiguration.current.screenWidthDp.dp
                val height = LocalConfiguration.current.screenHeightDp.dp
                BiliardScreen(
                    viewModel = viewModel(factory = BiliardViewModel.createViewModelFactory(width, height))
                )
            }
        }
    }
}
