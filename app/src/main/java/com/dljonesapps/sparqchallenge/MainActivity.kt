package com.dljonesapps.sparqchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dljonesapps.sparqchallenge.di.AppModule
import com.dljonesapps.sparqchallenge.ui.screens.PokemonListScreen
import com.dljonesapps.sparqchallenge.ui.theme.PokemonTheme
import com.dljonesapps.sparqchallenge.ui.viewmodel.PokemonListViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: PokemonListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AppModule.provideRepository(applicationContext)
        viewModel = PokemonListViewModel(repository)

        setContent {
            PokemonTheme {
                PokemonListScreen(viewModel)
            }
        }
    }
}
