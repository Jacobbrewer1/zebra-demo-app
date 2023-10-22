package com.github.jacobbrewer1.zebra_demo_app

import android.content.Intent
import android.os.Bundle
import android.view.View

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        toolbar = findViewById(R.id.toolbar)
        bottomNavView = findViewById(R.id.navigation_view)
        setToolbarTextAndNavListener()
    }

    fun logoutButtonPressed(view: View) {
        dialogHandler.displayYesNoDialog("settings_logout", "settings_logout_message", {
            clearMobileSession()
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }, null)
    }
}