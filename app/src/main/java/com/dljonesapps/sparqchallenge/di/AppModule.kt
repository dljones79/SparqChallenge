package com.dljonesapps.sparqchallenge.di

import android.content.Context
import androidx.room.Room
import com.dljonesapps.sparqchallenge.data.api.PokemonApi
import com.dljonesapps.sparqchallenge.data.db.PokemonDatabase
import com.dljonesapps.sparqchallenge.data.repository.PokemonRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    fun providePokemonApi(): PokemonApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApi::class.java)
    }

    fun provideRepository(context: Context): PokemonRepository {
        return PokemonRepository(
            api = providePokemonApi(),
            dao = PokemonDatabase.getDatabase(context).pokemonDao()
        )
    }
}
