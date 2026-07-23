package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.main.MainScaffold
import com.example.ui.theme.AuraPlanTheme
import com.example.ui.viewmodel.AuraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AuraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraPlanTheme {
                MainScaffold(viewModel = viewModel)
            }
        }
    }
}
