package com.lifecore.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import com.lifecore.app.ui.shell.LifeCoreAppShell
import com.lifecore.app.ui.theme.LifeCoreTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeCoreTheme {
                LifeCoreAppShell()
            }
        }
    }
}
