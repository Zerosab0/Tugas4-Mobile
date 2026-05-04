package com.tradein.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tradein.app.R
import com.tradein.app.model.DcaStrategy
import java.util.Locale

class StrategyAdapter(
    private val strategies: MutableList<DcaStrategy>,
    private val onItemClick: (DcaStrategy) -> Unit
) : RecyclerView.Adapter<StrategyAdapter.StrategyViewHolder>() {

    inner class StrategyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView:      CardView  = itemView.findViewById(R.id.card_strategy)
        val tvAsset:       TextView  = itemView.findViewById(R.id.tv_asset_symbol)
        val tvAmount:      TextView  = itemView.findViewById(R.id.tv_investment_amount)
        val tvFrequency:   TextView  = itemView.findViewById(R.id.tv_frequency)
        val tvDateRange:   TextView  = itemView.findViewById(R.id.tv_date_range)
        val tvNotes:       TextView  = itemView.findViewById(R.id.tv_notes)
        val tvBadge:       TextView  = itemView.findViewById(R.id.tv_frequency_badge)

        fun bind(strategy: DcaStrategy) {
            tvAsset.text     = strategy.assetSymbol
            tvAmount.text    = "\$${String.format(Locale.US, "%.2f", strategy.investmentAmount)}"
            tvFrequency.text = strategy.frequency
            tvBadge.text     = strategy.frequency.uppercase()
            tvDateRange.text = "${strategy.startDate}  →  ${strategy.endDate}"

            if (strategy.notes.isBlank()) {
                tvNotes.visibility = View.GONE
            } else {
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = strategy.notes
            }

            val badgeColor = when (strategy.frequency) {
                "Daily"   -> itemView.context.getColor(R.color.badge_daily)
                "Weekly"  -> itemView.context.getColor(R.color.badge_weekly)
                else      -> itemView.context.getColor(R.color.badge_monthly)
            }
            tvBadge.setBackgroundColor(badgeColor)

            cardView.setOnClickListener { onItemClick(strategy) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrategyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_strategy, parent, false)
        return StrategyViewHolder(view)
    }

    override fun onBindViewHolder(holder: StrategyViewHolder, position: Int) {
        holder.bind(strategies[position])
    }

    override fun getItemCount(): Int = strategies.size

    fun updateList(newList: List<DcaStrategy>) {
        strategies.clear()
        strategies.addAll(newList)
        notifyDataSetChanged()
    }
}
