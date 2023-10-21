package com.github.jacobbrewer1.zebra_demo_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.github.jacobbrewer1.zebra_demo_app.entities.MobileSession
import com.github.jacobbrewer1.zebra_demo_app.handlers.DialogHandler
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type
import kotlin.Boolean
import kotlin.Throws


abstract class BaseActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private val TAG = "BaseActivity"
    private val PREFS_NAME = "bthreePrefs"

    protected var sharedPreferences: SharedPreferences? = null

    protected var mobileSession: MobileSession? = null
    protected lateinit var dialogHandler: DialogHandler

    protected var gson: Gson? = null

    protected var bottomNavView: NavigationBarView? = null
    protected var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (gson == null) {
            gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, object : JsonSerializer<DateTime?> {
                override fun serialize(
                    json: DateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement? {
                    return JsonPrimitive(ISODateTimeFormat.dateTime().print(json))
                }
            }).registerTypeAdapter(DateTime::class.java, object : JsonDeserializer<DateTime?> {
                @Throws(JsonParseException::class)
                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): DateTime? {
                    return ISODateTimeFormat.dateTime().parseDateTime(json.getAsString()).withZone(DateTimeZone.UTC)
                }
            }).create()
        }

        dialogHandler = DialogHandler(this, supportFragmentManager)

        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        }

        if (mobileSession == null && (sharedPreferences != null && sharedPreferences!!.contains("MobileSession"))) {
            mobileSession =
                gson!!.fromJson(sharedPreferences!!.getString("MobileSession", "{}"), MobileSession::class.java)
        }

        if (mobileSession != null && mobileSession?.deviceId == null) {
            mobileSession?.deviceId = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent? = null

        when (item.itemId) {
            R.id.nav_item_home -> {
                Log.i(TAG, "onNavigationItemSelected: Home nav icon pressed")

                if (this !is MainActivity) {
                    intent = Intent(this, MainActivity::class.java)
                    finish()
                }
            }

            R.id.nav_item_sales -> {
                Log.i(TAG, "onNavigationItemSelected: Sales nav icon pressed")
            }

            R.id.nav_item_settings -> {
                Log.i(TAG, "onNavigationItemSelected: Settings nav icon pressed")

                if (this !is SettingsActivity) {
                    intent = Intent(this, SettingsActivity::class.java)
                }
            }
        }

        if (intent != null) {
            startActivity(intent)
        }

        return false
    }

    protected fun clearMobileSession() {
        this.mobileSession = null

        val editor: SharedPreferences.Editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    // Requires the toolbar and the bottomNavView to be set prior to the calling of this method otherwise it will not work
    protected fun setToolbarTextAndNavListener() {
        (toolbar?.findViewById<View>(R.id.toolbar_main_user) as TextView).text = mobileSession?.user?.firstName
        (toolbar?.findViewById<View>(R.id.toolbar_main_restaurant) as TextView).text = mobileSession?.store?.name

        bottomNavView?.setOnItemSelectedListener(this)
    }
}