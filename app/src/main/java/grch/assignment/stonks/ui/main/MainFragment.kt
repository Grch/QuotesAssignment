package grch.assignment.stonks.ui.main

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import grch.assignment.stonks.R
import grch.assignment.stonks.data.model.Product
import grch.assignment.stonks.databinding.MainFragmentBinding
import grch.assignment.stonks.utils.QuotesConstants
import timber.log.Timber

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
        setupPrefs()
        initRecyclerView()
        initViewModel()
        setupObserver()

    }

    private fun setupPrefs() {
        PreferenceManager.setDefaultValues(context, R.xml.preference, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun setupObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer { tick ->
            stocksAdapter.updateStockData(tick)
            Timber.d("Socket Fragment ticker $tick position ${tick.name}")
        })
    }

    private fun initRecyclerView() {
        stocksAdapter = StocksAdapter(ArrayList())
        binding.quotesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stocksAdapter
        }
        addProduct(Product.BTCUSD)
    }

    private fun addProduct(product: Product) {
        stocksAdapter.addProduct(QuotesConstants.generateInitialPair(product))
    }

    private fun initViewModel() {
        viewModel.subscribeStocks(listOf("SUBSCRIBE: BTCUSD"))
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
        Timber.d("SharepPrefs $key")
    }

}