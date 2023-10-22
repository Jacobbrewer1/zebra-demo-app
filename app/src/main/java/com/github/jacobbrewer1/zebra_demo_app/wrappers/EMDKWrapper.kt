package com.github.jacobbrewer1.zebra_demo_app.wrappers

import android.content.Context
import android.os.AsyncTask
import com.github.jacobbrewer1.zebra_demo_app.interfaces.IScanner
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.barcode.*
import com.symbol.emdk.barcode.StatusData.ScannerStates.*
import kotlin.concurrent.thread

class EMDKWrapper constructor(context: Context) :
    EMDKManager.EMDKListener,
    Scanner.StatusListener,
    Scanner.DataListener {

    var scannable: IScanner? = null

    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null
    private val initLock = Any()

    init {
        scannable = context as IScanner

        try {
            // Although this doesn't appear to do anything, it triggers the onOpened to run below!
            // RT-612 Fixed small leak by only providing ApplicationContext to EMDKManager rather than activity context
            EMDKManager.getEMDKManager(context.applicationContext, this@EMDKWrapper)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpened(emdkManager: EMDKManager) {
        this.emdkManager = emdkManager

        // This take a few milliseconds and causes the UI to skip frames so doing on a separate background.
        thread(start = true) {
            try {
                initialiseScanner()
            } catch (e: ScannerException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClosed() {
        // EMDKManager is closed abruptly. Call EmdkManager.release() to free the resources used by the current
        // EMDK instance.
        onPauseWrapped(true);
    }

    override fun onStatus(statusData: StatusData?) {
        var statusString = ""
        val state: StatusData.ScannerStates = statusData?.state ?: return

        when (state) {
            IDLE -> {
                statusString = statusData.friendlyName + " is enabled and idle..."
                try {
                    // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                    // may cause the scanner to pause momentarily before resuming the scanning.
                    // Hence, add some delay (>= 100ms) before submitting the next read.
                    try {
                        // CD 06/12/2018 I've made this 200 because this method is called after releasing the scanner
                        // and code below throws an exception
                        // if it gets there after a .release() but before it's actually finished releasing. 100ms
                        // wasn't enough, 200 appears to be.
                        Thread.sleep(200)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (scanner != null && scanner!!.isEnabled && !scanner!!.isReadPending) {
                        scanner!!.read()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    statusString = e.message.toString()
                }
            }

            WAITING -> statusString = "Scanner is waiting for trigger press..."
            SCANNING -> statusString = "Scanning..."
            DISABLED -> statusString = statusData.friendlyName + " is disabled."
            ERROR -> statusString = "An error has occurred."
            else -> {}
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        AsyncDataUpdate(scanner!!).execute(scanDataCollection)
    }

    // Method to initialize and enable Scanner and its listeners
    @Throws(ScannerException::class)
    private fun initialiseScanner() {
        synchronized(initLock) {
            if (scanner == null) {
                barcodeManager = emdkManager!!.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
                scanner = barcodeManager!!.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)

                // Add data and status listeners
                if (scanner != null) {
                    scanner!!.addDataListener(this)
                    scanner!!.addStatusListener(this)
                }

                // Hard trigger. When this mode is set, the user has to manually press the trigger on the device
                // after issuing the read call.
                if (scanner != null) {
                    scanner!!.triggerType = Scanner.TriggerType.HARD
                    scanner!!.enable()
                }
                if (scanner != null) {
                    val scannerConfig = scanner!!.config
                    scannerConfig.decoderParams.i2of5.enabled = true
                    scanner!!.config = scannerConfig
                }
                if (scanner != null && scanner!!.isEnabled && !scanner!!.isReadPending) {
                    scanner!!.read()
                }
            }
        }
    }

    fun onPauseWrapped(intentionalDestroy: Boolean) {
        synchronized(initLock) {
            //RT-612 if called from onDestroy make sure activity context is not retained
            if (intentionalDestroy) {
                scannable = null
            }

            if (scanner != null) {
                try {
                    if (scanner!!.isReadPending) {
                        scanner!!.cancelRead()
                    }

                    //RT-612 Make sure Listeners are removed on Pause
                    scanner!!.removeDataListener(this)
                    scanner!!.removeStatusListener(this)
                    scanner!!.disable()
                    scanner!!.release()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                scanner = null
            }

            if (emdkManager != null) {
                try {
                    emdkManager!!.release()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                emdkManager = null
            }
        }
    }


    inner class AsyncDataUpdate(scanner: Scanner) :
        AsyncTask<ScanDataCollection?, Void?, String>() {

        var scanner: Scanner?

        init {
            this.scanner = scanner
        }

        override fun doInBackground(vararg params: ScanDataCollection?): String {
            var statusStr = ""
            try {
                // RT-612 make sure a read can't be attempted from a location where the scanner is invalid
                if (scanner != null && scanner!!.isEnabled) {
                    if (!scanner!!.isReadPending) {
                        scanner!!.read()
                    }
                }
                val scanDataCollection = params[0]

                // The ScanDataCollection object gives scanning result and the
                // collection of ScanData. So check the data and its status
                if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
                    val scanData = scanDataCollection.scanData

                    // Iterate through scanned data and prepare the statusStr
                    for (data in scanData) {
                        val barcodeData = data.data
                        val labelType = data.labelType
                        statusStr = barcodeData
                    }
                }
            } catch (e: ScannerException) {
                e.printStackTrace()
            }

            return statusStr
        }

        override fun onPostExecute(barcode: String) {
            scannable?.onScan(barcode)
        }
    }
}