package grch.assignment.quotes.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import grch.assignment.quotes.data.model.Product
import grch.assignment.quotes.data.model.SocketResponse.Ticker
import grch.assignment.quotes.databinding.QuoteItemBinding
import grch.assignment.quotes.utils.QuotesConstants

class QuotesAdapter(private var list: ArrayList<Ticker.Tick>) :
    RecyclerView.Adapter<QuotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateQuoteData(tick: Ticker.Tick) {
        list.forEachIndexed { index, element ->
            if (element.product == tick.product) {
                list[index].ask = tick.ask
                list[index].bid = tick.bid
                list[index].spread = tick.spread
                notifyItemChanged(index)
            }
        }
    }

    fun addProduct(product: Product) {
        if (!containsProduct(product)) {
            list.add(QuotesConstants.generateInitialPair(product))
            notifyDataSetChanged()
        }
    }

    fun removeProduct(product: Product) {
        val it = list.iterator()

        while (it.hasNext()) {
            val value = it.next()
            if (value.product.name == product.name) {
                it.remove()
            }
        }
        notifyDataSetChanged()
    }

    private fun containsProduct(product: Product): Boolean {
        for (item in list) {
            if (item.product.name == product.name) {
                return true
            }
        }
        return false
    }

    class ViewHolder(private val binding: QuoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tick: Ticker.Tick) {
            binding.quoteName.text = tick.product.displayCode
            binding.quoteBid.text = tick.bid
            binding.quoteAsk.text = tick.ask
            binding.quoteSpread.text = tick.spread
        }
    }
}