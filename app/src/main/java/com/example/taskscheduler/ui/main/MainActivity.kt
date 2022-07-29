package com.example.taskscheduler.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.contains
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val tasksAdapterViewModel: TasksAdapterViewModel by viewModels()

    private val viewModel: MainActivityViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.nav_host_fragment_content_main) }

    private var statusMenu = R.menu.empty
        set(value) {
            field = value
            invalidateOptionsMenu()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "main activity created".log()

        lifecycleScope.launch {
            viewModel.intentChannel.send(MainActivityViewModel.Intent.Synchronize)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        observeViewModel()

//        if ((tasksAdapterViewModel.taskRepository as? TaskRepository)?.initEasyFiresoreSynchronization() == true) {
//            "firesoreSynchronization starts".log()
//        } else {
//            "firesoreSynchronization not possible".log()
//        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.syncStateFlow.collect { state ->
                statusMenu = when (state) {
                    is MainActivityViewModel.SyncState.Done -> R.menu.sync_done
                    is MainActivityViewModel.SyncState.InProcess -> R.menu.sync_in_process
                    is MainActivityViewModel.SyncState.Error -> R.menu.sync_impossible
                    is MainActivityViewModel.SyncState.NotAuth -> R.menu.empty
                }
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            findItem(R.id.sync_done)?.also { return false }
            findItem(R.id.sync_impossible)?.also { return false }
            findItem(R.id.sync_in_process)?.also { return false }
        }
        menuInflater.inflate(statusMenu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        "navigate up pressed".log()
        return when(navController.currentDestination?.id) {
            R.id.fragment_tasks -> NavigationUI.navigateUp(navController, binding.drawerLayout)
            else -> {
                onBackPressed()
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId.let { it == R.id.sync_done || it == R.id.sync_impossible }) {
            lifecycleScope.launch {
                viewModel.intentChannel.send(MainActivityViewModel.Intent.Synchronize)
            }
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

//    override fun onDestroy() {
//        (tasksAdapterViewModel.taskRepository as? TaskRepository)?.finishEasyFireSoreSynchronization()
//        "firesoreSynchronization ends".log()
//        super.onDestroy()
//    }

    private fun<T> T.log(msj: String? = null) = apply {
        Log.i("MainActivity", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}
