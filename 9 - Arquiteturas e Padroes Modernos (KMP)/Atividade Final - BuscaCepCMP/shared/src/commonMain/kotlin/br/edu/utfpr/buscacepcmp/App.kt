package br.edu.utfpr.buscacepcmp

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import br.edu.utfpr.buscacepcmp.ui.BuscaCepScreen
import br.edu.utfpr.buscacepcmp.ui.theme.BuscaCepTheme

@Composable
@Preview
fun App() {
    BuscaCepTheme {
        BuscaCepScreen()
    }
}