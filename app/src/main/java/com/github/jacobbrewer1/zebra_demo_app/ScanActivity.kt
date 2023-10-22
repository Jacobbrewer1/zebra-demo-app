package com.github.jacobbrewer1.zebra_demo_app

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.github.jacobbrewer1.zebra_demo_app.interfaces.IScanner

class ScanActivity : BaseActivity(), IScanner {
    private val tag = "ScanActivity"

    private var barcodeDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan)

        barcodeDisplay = findViewById(R.id.barcode_display)

        barcodeDisplay?.text = getString(R.string.scan_waiting_for_scan)

        toolbar = findViewById(R.id.toolbar)
        bottomNavView = findViewById(R.id.navigation_view)
        setToolbarTextAndNavListener()
    }

    override fun onScan(data: String) {
        Log.d(tag, "onScan: $data")
        barcodeDisplay?.text = data
    }
}