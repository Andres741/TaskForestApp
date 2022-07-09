package com.example.taskscheduler.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val tasksAdapterViewModel: TasksAdapterViewModel by viewModels()

    private val tasksAdapterViewModel: MainActivityViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.nav_host_fragment_content_main) }

    private val mainFrag by lazy { R.id.fragment_tasks }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)


        if (tasksAdapterViewModel.taskRepository.initEasyFiresoreSynchronization()) {
            "firesoreSynchronization starts".log()
        } else {
            "firesoreSynchronization not possible".log()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return when(navController.currentDestination?.id) {
            mainFrag -> NavigationUI.navigateUp(navController, binding.drawerLayout)
            else -> {
                onBackPressed()
                true
            }
        }
    }

    override fun onDestroy() {
        tasksAdapterViewModel.taskRepository.finishEasyFireSoreSynchronization()
        "firesoreSynchronization ends".log()
        super.onDestroy()
    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("MainActivity", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}
