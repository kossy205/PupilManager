package com.kosiso.pupilmanager

import android.app.Application
import androidx.work.WorkManager
import com.kosiso.pupilmanager.utils.NetworkUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ApplicationClass : Application() {
//    @Inject lateinit var workManager: WorkManager
    override fun onCreate() {
        super.onCreate()
//        NetworkUtils.getInstance(this).registerNetworkCallback()
    }
}