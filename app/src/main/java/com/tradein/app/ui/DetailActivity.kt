package com.tradein.app.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tradein.app.R
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.apply {
            title = "Strategy Detail"
            setDisplayHomeAsUpEnabled(true)
        }

        val asset   = intent.getStringExtra("asset") ?: "-"
        val amount  = intent.getDoubleExtra("amount", 0.0)
        val freq    = intent.getStringExtra("freq") ?: "-"
        val start   = intent.getStringExtra("start") ?: "-"
        val end     = intent.getStringExtra("end") ?: "-"
        val notes   = intent.getStringExtra("notes") ?: ""

        findViewById<TextView>(R.id.tv_detail_asset).text     = asset
        findViewById<TextView>(R.id.tv_detail_amount).text    =
            "\$${String.format(Locale.US, "%.2f", amount)} per $freq"
        findViewById<TextView>(R.id.tv_detail_daterange).text = "$start  →  $end"
        findViewById<TextView>(R.id.tv_detail_notes).text     = notes.ifBlank { "No notes." }

        // Simple projected total (for display only – not a real backtest)
        val days = daysBetween(start, end)
        val numPurchases = when (freq) {
            "Daily"   -> days
            "Weekly"  -> days / 7
            else      -> days / 30
        }
        val totalInvested = numPurchases * amount
        findViewById<TextView>(R.id.tv_detail_total).text =
            "Est. purchases: $numPurchases  •  Total invested: \$${String.format(Locale.US, "%.2f", totalInvested)}"
    }

    private fun daysBetween(start: String, end: String): Long {
        return try {
            val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val d1 = fmt.parse(start)?.time ?: 0L
            val d2 = fmt.parse(end)?.time   ?: 0L
            if (d2 > d1) (d2 - d1) / (1000 * 60 * 60 * 24) else 0L
        } catch (e: Exception) { 0L }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
