package com.wadud.gads.ui.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wadud.gads.network.LoadingStatus
import com.wadud.gads.network.Result
import com.wadud.gads.network.repository.ApiRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class SubmissionViewModel @ViewModelInject constructor(val repository: ApiRepository) :
    ViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean>
        get() = _success

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus


    fun makeSubmission(email: String, name: String, lastName: String, githubLink: String) {
        _loadingStatus.value = LoadingStatus.Loading("making your Submission ...")
        viewModelScope.launch {
            when (val result = repository.makeSubmission(email, name, lastName, githubLink)) {
                is Result.Success -> {
                    _success.value = true
                    _loadingStatus.value = LoadingStatus.Success
                }
                is Result.Error -> {
                    _success.value = false
                    _loadingStatus.value = LoadingStatus.Error(result.message)
                }
            }
        }


    }

    fun resetSuccessValue() {
        _success.value = null
    }

}