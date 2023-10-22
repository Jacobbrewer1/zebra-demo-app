package com.github.jacobbrewer1.zebra_demo_app.entities

class MobileSession {

    // Device ID of the device the user is logged in on.
    var deviceId: String? = null

    // User that is logged in.
    var user: User? = null

    // Store the user is logged into.
    var store: Store? = null
}