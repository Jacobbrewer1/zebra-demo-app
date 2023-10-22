package com.github.jacobbrewer1.zebra_demo_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.github.jacobbrewer1.zebra_demo_app.entities.MobileSession
import com.github.jacobbrewer1.zebra_demo_app.handlers.DialogHandler
import com.github.jacobbrewer1.zebra_demo_app.interfaces.IScanner
import com.github.jacobbrewer1.zebra_demo_app.wrappers.EMDKWrapper
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.ISODateTimeFormat
import java.lang.reflect.Type


abstract class BaseActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private val tag = "BaseActivity"
    private val prefsName = "demo_prefs"

    protected var sharedPreferences: SharedPreferences? = null

    protected var mobileSession: MobileSession? = null
    protected lateinit var dialogHandler: DialogHandler

    protected var gson: Gson? = null

    protected var bottomNavView: NavigationBarView? = null
    protected var toolbar: Toolbar? = null

    private var emdkWrapper: EMDKWrapper? = null

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
            sharedPreferences = getSharedPreferences(prefsName, MODE_PRIVATE)
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

        // If "this" implements the IScanner interface, then we need to register the scanner with the EMDKWrapper.
        if (this is IScanner) {
            // Get the device name to check that it is a Zebra device.
            val deviceName = getDeviceName()

            if (deviceName.contains("Zebra")) {
                // If the device is a Zebra device, then we need to register the scanner with the EMDKWrapper.
                if (emdkWrapper == null) {
                    Log.d(tag, "onResume: Creating EMDKWrapper")
                    emdkWrapper = EMDKWrapper(this)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent? = null

        when (item.itemId) {
            R.id.navigation_home -> {
                Log.i(tag, "onNavigationItemSelected: Home nav icon pressed")

                if (this !is MainActivity) {
                    intent = Intent(this, MainActivity::class.java)
                    finish()
                }
            }

            R.id.navigation_dashboard -> {
                Log.i(tag, "onNavigationItemSelected: Dashboard nav icon pressed")
            }

            R.id.navigation_settings -> {
                Log.i(tag, "onNavigationItemSelected: Settings nav icon pressed")

//                if (this !is SettingsActivity) {
//                    intent = Intent(this, SettingsActivity::class.java)
//                }
            }
        }

        if (intent != null) {
            startActivity(intent)
        }

        return false
    }

    protected fun clearMobileSession() {
        this.mobileSession = null

        val editor: SharedPreferences.Editor = getSharedPreferences(prefsName, MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }

    // Requires the toolbar and the bottomNavView to be set prior to the calling of this method otherwise it will not work
    protected fun setToolbarTextAndNavListener() {
        (toolbar?.findViewById<View>(R.id.toolbar_main_user) as TextView).text = " " + mobileSession?.user?.firstName
        (toolbar?.findViewById<View>(R.id.toolbar_main_store) as TextView).text = " " + mobileSession?.store?.name

        bottomNavView?.setOnItemSelectedListener(this)
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true
        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(c.uppercaseChar())
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }
        return phrase.toString()
    }
}