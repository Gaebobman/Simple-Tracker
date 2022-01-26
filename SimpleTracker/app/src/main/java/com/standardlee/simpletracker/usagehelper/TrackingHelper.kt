package com.standardlee.simpletracker.usagehelper

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TrackingHelper() {
    private lateinit var networkStatsManager: NetworkStatsManager
    private lateinit var packageManager: PackageManager
    private val packageInfoList: MutableList<PackageInfo> = mutableListOf()

    constructor(
        networkStatsManager: NetworkStatsManager,
        packageManager: PackageManager
    ) : this() {
        this.networkStatsManager=networkStatsManager
        this.packageManager= packageManager
        this.setPackageInfoList()   // 생성 후 앱 목록 불러오기
    }

    private fun setPackageInfoList() {
        // 설치된 모든 어플 목록 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
            for (i in list) {
                packageInfoList.add(i)
            }
        }
    }

    private fun setDailyDataUsageForAllApp() {
        CoroutineScope(Dispatchers.IO).launch {
            for(info in packageInfoList){
                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    0,
                    System.currentTimeMillis(),
                    packageManager.getApplicationInfo(info.packageName, 0).uid
                )
            }
        }
    }

    private fun getWeeklyDataUsageForAllApp() {

    }

    fun getDataUsageForASingleApp() {
        CoroutineScope(Dispatchers.IO).launch {
            val packageName = "com.android.chrome"  // 패키지명
            val info = packageManager.getApplicationInfo(packageName, 0)    // ApplicationInfo
            val uid = info.uid  // 앱의 UID
            val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                null,
                0,
                System.currentTimeMillis(),
                uid
            )

            val bucketMobile = NetworkStats.Bucket()
            var rxtxMobile = 0L

            while (nwStatsMobile.hasNextBucket()) {
                nwStatsMobile.getNextBucket(bucketMobile)
                rxtxMobile += bucketMobile.rxBytes
                rxtxMobile += bucketMobile.txBytes
            }

            val label = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()

            Log.d("TrackingHelper", "App: ${label} 사용량: ${rxtxMobile}")
        }
    }
}