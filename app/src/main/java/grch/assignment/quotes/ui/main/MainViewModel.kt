package grch.assignment.quotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import grch.assignment.quotes.data.model.Product
import grch.assignment.quotes.data.model.SocketResponse.Ticker.Tick
import grch.assignment.quotes.data.repository.QuotesRepository
import grch.assignment.quotes.utils.toLiveData
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: QuotesRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    private val _allQuotes = MutableLiveData<Tick>()
    val allQuotes: LiveData<Tick> = _allQuotes

    //should be observed in a separate viewModel, I've put it here just for ability to observe a single product
    private val selectedProduct = MutableLiveData<Product>()
    val selectedProductQuotes: LiveData<Tick>
        get() = Transformations.switchMap(selectedProduct) { product ->
            repository.observeTicker().filter { ticker -> ticker.product == product }
                .toLiveData()
        }

    init {
        observeStocks()
    }
    fun subscribeProduct(productCode: String) {
        repository.subscribeProduct(productCode)
    }

    fun unsubscribeProduct(productCode: String) {
        repository.unsubscribeProduct(productCode)
    }

    fun observeSingleProduct(product: Product) {
        selectedProduct.postValue(product)
    }

    private fun observeStocks() {
        disposables.add(repository.observeTicker()
            .subscribe({
                _allQuotes.postValue(it)
            }, {
                Timber.e("Socket viewModel error")
            }))
    }
}