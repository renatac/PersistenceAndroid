package com.example.persitenceandnetwork

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity4 : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "sharedPreference"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        takeAndSaveSharedPreference()

        recoverDatasAtSharedPreference()

    }

    private fun recoverDatasAtSharedPreference() {
        val prefs: SharedPreferences = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        //Observe que é preciso especificar um valor padrão que será retornado caso o preference não exista
        val isNightMode = prefs.getBoolean("night_mode", false)
        val userName = prefs.getString("user_name", null)
        Log.d(MainActivity.TAG, "isNightMode - $isNightMode, userName - $userName")
    }

    private fun takeAndSaveSharedPreference() {
        //Obtendo um objeto sharedPreference:
        //O modo padrão é o MODO_PRIVATE ou simplesmente zero (0)
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        //Salvando dados no SharedPreferences
        val editor = prefs.edit()
        editor.apply{
            putBoolean("night_mode", true)
            putString("user_name", "user 007")
            apply() //editor.apply salva os dados de forma assíncrona, enquanto editor.commit() é síncrono.
        }
    }
}