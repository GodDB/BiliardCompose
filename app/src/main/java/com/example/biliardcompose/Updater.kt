package com.example.biliardcompose

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class Updater(
    private val FPS: Int = 60
) {

    private var job: Job? = null

    suspend fun start(
        block: () -> Unit
    ) {
        stop()
        coroutineScope {
            job = launch {
                while (true) {
                    job?.ensureActive()
                    block()
                    delay(1000L / FPS)
                }
            }
        }
    }

    suspend fun start(
        scalar : Int,
        block : (distance : Int) -> Unit
    ) {
        stop()
        var count = 0
        coroutineScope {
            job = launch {
                while (count < 17) {
                    job?.ensureActive()
                    val distance = (scalar / (1000L / FPS)) * 0.98
                    block(distance.toInt())
                    delay(1000L / FPS)
                    count++
                }
                stop()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
