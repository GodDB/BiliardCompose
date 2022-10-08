package com.example.biliardcompose.module

import com.example.biliardcompose.BiliardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelModule = module {

    viewModel { BiliardViewModel() }
}
