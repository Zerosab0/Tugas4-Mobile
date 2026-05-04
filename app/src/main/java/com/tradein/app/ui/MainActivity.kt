package com.tradein.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tradein.app.R
import com.tradein.app.adapter.StrategyAdapter
import com.tradein.app.database.DatabaseHelper
import com.tradein.app.model.DcaStrategy

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView:  RecyclerView
    private lateinit var adapter:       StrategyAdapter
    private lateinit var dbHelper:      DatabaseHelper
    private lateinit var tvEmpty:       View
    private lateinit var tvCount:       TextView
    private lateinit var fabAdd:        FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper     = DatabaseHelper(this)
        tvEmpty      = findViewById(R.id.tv_empty)
        tvCount      = findViewById(R.id.tv_strategy_count)
        fabAdd       = findViewById(R.id.fab_add)
        recyclerView = findViewById(R.id.recycler_strategies)

        setupRecyclerView()
        loadStrategies()

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStrategyActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadStrategies()
    }

    private fun setupRecyclerView() {
        adapter = StrategyAdapter(mutableListOf()) { strategy ->
            showStrategyDetail(strategy)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadStrategies() {
        val list = dbHelper.getAllStrategies()
        adapter.updateList(list)

        if (list.isEmpty()) {
            tvEmpty.visibility      = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility      = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        val count = list.size
        tvCount.text = "$count ${if (count == 1) "Strategy" else "Strategies"}"
    }

    private fun showStrategyDetail(strategy: DcaStrategy) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id",     strategy.id)
        intent.putExtra("asset",  strategy.assetSymbol)
        intent.putExtra("amount", strategy.investmentAmount)
        intent.putExtra("freq",   strategy.frequency)
        intent.putExtra("start",  strategy.startDate)
        intent.putExtra("end",    strategy.endDate)
        intent.putExtra("notes",  strategy.notes)
        startActivity(intent)
    }
}
