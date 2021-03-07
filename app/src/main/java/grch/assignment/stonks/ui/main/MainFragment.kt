package grch.assignment.stonks.ui.main

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import grch.assignment.stonks.R
import grch.assignment.stonks.data.model.Product
import grch.assignment.stonks.databinding.MainFragmentBinding
import grch.assignment.stonks.utils.AppBackgroundListener

@AndroidEntryPoint
class MainFragment : Fragment(), OnSharedPreferenceChangeListener {

    private lateinit var binding: MainFragmentBinding
    private lateinit var stocksAdapter: StocksAdapter
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainFragmentBinding.bind(view)
        initRecyclerView()
        setupPrefs()
        setupObserver()

        setupLifecycleObserver()
    }

    private fun setupLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppBackgroundListener())
    }

    private fun setupPrefs() {
        PreferenceManager.setDefaultValues(context, R.xml.preference, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        Product.values().forEach { product ->
            val preferenceValue = sharedPreferences.getBoolean(product.code, false)
            if (preferenceValue) {
                addProduct(product)
            } else {
                removeProduct(product)
            }
        }
    }

    private fun setupObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer { tick ->
            stocksAdapter.updateStockData(tick)
        })
    }

    private fun initRecyclerView() {
        stocksAdapter = StocksAdapter(ArrayList())
        binding.quotesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stocksAdapter
        }
    }

    private fun addProduct(product: Product) {
        stocksAdapter.addProduct(product)
        viewModel.subscribeStocks(product.name)
    }

    private fun removeProduct(product: Product) {
        stocksAdapter.removeProduct(product)
        viewModel.unsubscribeStocks(product.name)
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val prefReference = sharedPreferences?.getBoolean(key, false)
        Product.values().forEach { product ->
            if (product.name == key.orEmpty()) {
                when (prefReference) {
                    true -> addProduct(product)
                    false -> removeProduct(product)
                }
            }
        }
    }

}