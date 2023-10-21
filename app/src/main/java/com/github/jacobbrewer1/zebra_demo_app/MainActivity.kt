package com.github.jacobbrewer1.zebra_demo_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import android.widget.ViewFlipper

class MainActivity : BaseActivity() {
    private val TAG: String = "MainActivity"

    private var pageFlipper: ViewFlipper? = null
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Logged in user check.
        if (mobileSession == null && sharedPreferences?.contains("MobileSession") == false) {
            val intent = Intent(this, LoginActivity::class.java)

            if (getIntent().extras != null) {
                if (getIntent().extras!!.containsKey("MobileSession")) {
                    intent.putExtra("MobileSession", getIntent().extras!!.getString("MobileSession"))
                }
            }

            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        bottomNavView = findViewById(R.id.navigation_view)
        setToolbarTextAndNavListener()

        pageFlipper = findViewById(R.id.page_flipper)
    }

    fun pageSelectorClicked(view: View) {
        val nextIndex = view.tag.toString().toInt()
        if (nextIndex > currentIndex) {
            pageFlipper?.inAnimation = AnimationUtils.loadAnimation(view.context, R.anim.flip_in_right)
            pageFlipper?.outAnimation = AnimationUtils.loadAnimation(view.context, R.anim.flip_out_left)
        } else {
            pageFlipper?.inAnimation = AnimationUtils.loadAnimation(view.context, R.anim.flip_in_left)
            pageFlipper?.outAnimation = AnimationUtils.loadAnimation(view.context, R.anim.flip_out_right)
        }

        currentIndex = nextIndex
        pageFlipper?.displayedChild = nextIndex
        resetPageSelectors(nextIndex)
    }

    private fun resetPageSelectors(displayedIndex: Int) {
        val flipperButton1: Button = findViewById(R.id.page_selector_button1)
        flipperButton1.isEnabled = displayedIndex != 0
    }

    fun navigate(view: View) {
        var intent: Intent? = null

        when (view.id) {
            R.id.menu_order_button -> {
                Log.i(TAG, "navigate: Order button pressed, navigating")

                intent = Intent(this, OrderActivity::class.java)
            }

            else -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.general_unknown_activity),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (intent != null) {
            startActivity(intent)
        }
    }
}