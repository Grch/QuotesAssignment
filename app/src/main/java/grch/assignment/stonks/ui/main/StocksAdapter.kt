package grch.assignment.stonks.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import grch.assignment.stonks.data.model.SocketResponse.Ticker
import grch.assignment.stonks.databinding.QuoteItemBinding

class StocksAdapter(private var list: ArrayList<Ticker.Tick>) :
    RecyclerView.Adapter<StocksAdapter.ViewHolder>() {

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

    fun updateStockData(tick: Ticker.Tick) {
        list.forEachIndexed { index, element ->
            if (element.name == tick.name) {
                list[index].ask = tick.ask
                list[index].bid = tick.bid
                list[index].spread = tick.spread
                notifyItemChanged(index)
            }
        }
    }

    fun addProduct(tick: Ticker.Tick) {
        list.add(tick)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: QuoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tick: Ticker.Tick) {
            binding.quoteName.text = tick.name.displayCode
            binding.quoteBid.text = tick.bid
            binding.quoteAsk.text = tick.ask
            binding.quoteSpread.text = tick.spread
        }
    }
}