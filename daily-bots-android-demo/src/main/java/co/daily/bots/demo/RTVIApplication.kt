package co.daily.bots.demo

import android.app.Application

class RTVIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.initAppStart(this)
    }
}