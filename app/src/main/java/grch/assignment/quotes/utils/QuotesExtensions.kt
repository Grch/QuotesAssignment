package grch.assignment.quotes.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable


fun <T> Flowable<T>.toLiveData(): LiveData<T> =
    MutableLiveData<T>().apply {
        this@toLiveData.subscribe { postValue(it) }
    }