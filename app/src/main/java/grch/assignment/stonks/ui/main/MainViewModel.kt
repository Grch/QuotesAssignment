package grch.assignment.stonks.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import grch.assignment.stonks.data.model.SocketResponse
import grch.assignment.stonks.data.repository.StocksRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: StocksRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
    private var subscriptionsLiveData: MutableLiveData<String> = MutableLiveData()

    private val _state = MutableLiveData<SocketResponse.Ticker.Tick>()
    val state: LiveData<SocketResponse.Ticker.Tick> = _state

    init {
        observeStocks()
    }
    fun subscribeStocks(stock: String) {
        repository.subscribeStocks(stock)
    }

    fun unsubscribeStocks(stock: String) {
        repository.unsubscribeStocks(stock)
    }

    fun observeStocks() {
        disposables.add(repository.observeTicker()
            .subscribe({
                _state.postValue(it)
            }, {
                Timber.e("Socket viewModel error")
            }))
    }
}