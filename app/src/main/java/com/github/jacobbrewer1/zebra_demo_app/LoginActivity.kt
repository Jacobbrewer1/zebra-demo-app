package com.github.jacobbrewer1.zebra_demo_app


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.jacobbrewer1.zebra_demo_app.entities.MobileSession
import com.github.jacobbrewer1.zebra_demo_app.entities.Store
import com.github.jacobbrewer1.zebra_demo_app.entities.User

class LoginActivity : BaseActivity() {

    private var usernameText: EditText? = null
    private var passwordText: EditText? = null
    private var loginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        usernameText = findViewById(R.id.login_username)
        passwordText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_loginButton)

        if (isNetwork(applicationContext)) {
            Toast.makeText(applicationContext, "Internet Connected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Internet Is Not Connected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return (netInfo != null && netInfo.isConnectedOrConnecting)
    }

    fun loginButtonPressed(view: View) {
        if (usernameText?.text.toString() == "" || passwordText?.text.toString() == "") {
            dialogHandler.displayAlertDialog(
                getString(R.string.login_error_title),
                getString(R.string.login_error_text)
            )
            return
        }

        // TODO: Implement login task.
//        val loginTask: LoginTask = LoginTask(
//            usernameText?.text.toString(), passwordText?.text.toString(),
//            dialogHandler, applicationContext, gson!!, sharedPreferences!!.edit(), this
//        ) {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        loginTask.runTask()

        createMobileSession()

        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun createMobileSession() {
        val mobileSession: MobileSession = MobileSession()

        val user: User = User()
        user.firstName = usernameText?.text.toString()

        mobileSession.user = user

        val store: Store = Store()
        store.name = "Demo Store"

        mobileSession.store = store

        sharedPreferences?.edit()?.putString("MobileSession", gson?.toJson(mobileSession))?.apply()
    }
}
