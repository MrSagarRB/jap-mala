package com.sagar.japmala

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sagar.japmala.ui.HomeScreen
import com.sagar.japmala.ui.JapSplash
import com.sagar.japmala.ui.JapTheme

class MainActivity : ComponentActivity() {
    private val vm: JapViewModel by viewModels()
    private var homeVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JapTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                homeVisible = !showSplash
                Crossfade(targetState = showSplash, animationSpec = tween(700), label = "splash") { splash ->
                    if (splash) JapSplash { showSplash = false } else HomeScreen(vm)
                }
            }
        }
    }

    private fun isCountingVolumeKey(keyCode: Int) =
        homeVisible && vm.state.value.volumeKeys &&
            (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (isCountingVolumeKey(keyCode)) {
            if (event.repeatCount == 0) vm.increment()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean =
        if (isCountingVolumeKey(keyCode)) true else super.onKeyUp(keyCode, event)
}
