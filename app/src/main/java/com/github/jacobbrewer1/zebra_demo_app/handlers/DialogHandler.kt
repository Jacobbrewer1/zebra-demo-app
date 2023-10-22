package com.github.jacobbrewer1.zebra_demo_app.handlers

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.content.res.Configuration
import android.text.InputType
import android.text.util.Linkify
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.github.jacobbrewer1.zebra_demo_app.R
import java.util.*


class DialogHandler constructor(context: Context, fragmentManager: FragmentManager) {

    private val context: Context
    private val fragmentManager: FragmentManager

    private var alertDialog: AlertDialog? = null

    var editTextValue: String? = null
        get() = field

    var numberPickerValue: Int? = null

    init {
        this.context = context
        this.fragmentManager = fragmentManager
    }

    fun displayAlertDialog(title: String?, message: String?) {
        displayAlertDialog(title, message, null)
    }

    fun displayAlertDialog(title: String?, message: String?, closeAction: Runnable?, vararg params: Any?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getResourceString(title))
        builder.setMessage(getResourceString(message, *params))
        builder.setCancelable(false)

        if (closeAction != null) {
            builder.setPositiveButton(
                R.string.general_ok,
                DialogInterface.OnClickListener { dialog, which ->
                    closeAction.run()
                }
            )
        } else {
            builder.setPositiveButton(R.string.general_ok, null)
        }

        alertDialog = builder.create()

        // Change the colour of the OK button.
        alertDialog!!.setOnShowListener(OnShowListener {
            alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.dialog_text))
            Linkify.addLinks((alertDialog!!.findViewById<View>(android.R.id.message) as TextView), Linkify.ALL)
            (alertDialog!!.findViewById<View>(android.R.id.message) as TextView).setLinkTextColor(
                context.getColor(
                    R.color.selected_blue
                )
            )
        })

        alertDialog?.show()
    }

    fun displayYesNoDialog(
        title: String,
        message: String,
        yesAction: Runnable?,
        noAction: Runnable?,
        vararg params: Objects
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getResourceString(title))
        builder.setMessage(getResourceString(message, *params))
        builder.setCancelable(false)

        if (yesAction != null) {
            builder.setPositiveButton(R.string.general_yes,
                DialogInterface.OnClickListener { dialog, which -> yesAction.run() })
        } else {
            builder.setPositiveButton(R.string.general_yes, null)
        }

        if (noAction != null) {
            builder.setNegativeButton(R.string.general_no,
                DialogInterface.OnClickListener { dialog, which -> noAction.run() })
        } else {
            builder.setNegativeButton(R.string.general_no, null)
        }

        alertDialog = builder.create()

        // Change the colour of the Yes/No buttons.

        // Change the colour of the Yes/No buttons.
        alertDialog?.setOnShowListener(OnShowListener {
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                ?.setTextColor(context.getColor(R.color.dialog_text))
            alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
                ?.setTextColor(context.getColor(R.color.dialog_text))
        })

        alertDialog?.show()
    }

    fun displayEditTextDialog(title: String, message: String, onClose: Runnable?, vararg params: Objects) {
        editTextValue = null
        val alert = AlertDialog.Builder(context)

        val edittext = EditText(context)
        alert.setTitle(title)
        alert.setMessage(message)

        alert.setView(edittext)

        alert.setPositiveButton(context.getString(R.string.general_ok)) { dialog, whichButton ->
            val gotValue = edittext.text.toString()
            editTextValue = gotValue

            onClose?.run()
        }

        alert.show()
    }

    fun displayNumberPickerDialog(title: String, message: String, onClose: Runnable?, vararg params: Objects) {
        numberPickerValue = null
        val alert = AlertDialog.Builder(context)

        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.setRawInputType(Configuration.KEYBOARD_12KEY)

        alert.setTitle(title)
        alert.setMessage(message)

        alert.setView(editText)

        alert.setPositiveButton(context.getString(R.string.general_ok)) { dialog, whichButton ->
            val gotValue = editText.text.toString()
            if (gotValue != "") {
                numberPickerValue = gotValue.toInt()
            } else {
                numberPickerValue = 0
            }

            onClose?.run()
        }

        alert.show()
    }

    fun getResourceString(name: String?, vararg params: Any?): String? {
        var result = name
        if (name.isNullOrEmpty()) {
            result = context.getString(R.string.app_name) // todo change this string
        } else {
            val resID = context.resources.getIdentifier(name, "string", context.packageName)
            if (resID != 0) {
                result = context.getString(resID, *params)
            }
        }
        return result
    }
}