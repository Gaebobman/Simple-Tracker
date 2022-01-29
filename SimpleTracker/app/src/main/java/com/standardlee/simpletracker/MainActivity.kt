package com.standardlee.simpletracker

import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.standardlee.simpletracker.usagehelper.TrackingHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (!checkForPermission()) {
            Toast.makeText(
                this,
                "권한이 없어요 허용해주세요",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        CoroutineScope(Dispatchers.IO).launch {
            val networkStatsManager =
                applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
            val packageManager = applicationContext.packageManager
            val trackingHelper = TrackingHelper(networkStatsManager, packageManager)
            trackingHelper.setPackageInfoList()
            trackingHelper.setDailyDataUsageForAllApp()
            trackingHelper.setWeeklyDataUsageForAllApp()
            trackingHelper.setMonthlyDataUsageForAllApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

}