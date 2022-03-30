package com.example.taskscheduler.ui.main.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(): ViewModel() {


    class Factory(): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SecondViewModel::class.java)) {
                return SecondViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
