package com.example.perludilindungi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PerluDilindungi)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()


        val navController = findNavController(R.id.navHostFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val checkInButton = findViewById<FloatingActionButton>(R.id.toCheckInButton)

        bottomNavigationView.setOnItemSelectedListener{item ->
            when(item.itemId){
                R.id.action_news -> {
                    navController.navigate(R.id.newsFragment)
                    true
                }

                R.id.action_faskes -> {
                    navController.navigate(R.id.faskesFragment)
                    true
                }

                R.id.action_bookmark -> {
                    navController.navigate(R.id.boomarkFragment)
                    true
                }
                else -> false
            }
        }

        checkInButton.setOnClickListener {
            checkInButton.visibility = View.GONE
            bottomNavigationView.visibility = View.GONE
            navController.navigate(R.id.checkInFragment)
        }


    }
}