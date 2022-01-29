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
import java.util.*

const val TAG = "TrackingHelper"

class TrackingHelper() {
    private val INTERVAL_DAY: Long = 1000 * 60 * 60 * 24
    private val INTERVAL_WEEK: Long = INTERVAL_DAY * 7
    private val INTERVAL_MONTH: Long = INTERVAL_WEEK * 30
    private val NOW: Long = System.currentTimeMillis()
    private lateinit var networkStatsManager: NetworkStatsManager
    private lateinit var packageManager: PackageManager
    private val _packageInfoList: MutableList<PackageInfo> = mutableListOf()
    val packageInfoList: List<PackageInfo> = _packageInfoList
    private val _dailyDataUsageMap: MutableMap<String, Long> = mutableMapOf()
    val dailyDataUsageMap: Map<String, Long> = _dailyDataUsageMap
    private val _weeklyDataUsageMap: MutableMap<String, Long> = mutableMapOf()
    val weeklyDataUsageMap: Map<String, Long> = _weeklyDataUsageMap
    private val _monthlyDataUsageMap: MutableMap<String, Long> = mutableMapOf()
    val monthlyDataUsageMap: Map<String, Long> = _monthlyDataUsageMap

    constructor(
        networkStatsManager: NetworkStatsManager,
        packageManager: PackageManager
    ) : this() {
        this.networkStatsManager = networkStatsManager
        this.packageManager = packageManager
    }

    fun setPackageInfoList() {
        // 설치된 모든 어플 목록 가져오기
        var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in list) {
            _packageInfoList.add(i)
        }
    }

    fun setDailyDataUsageForAllApp() {
        // 실행한 날의 00시 00분을 Epoch time 으로 저장
        val todayStart = (Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time)

        for (info in packageInfoList) {
            val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                null,
                todayStart,
                NOW,
                packageManager.getApplicationInfo(info.packageName, 0).uid
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
                    info.packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
            _dailyDataUsageMap[label] = rxtxMobile
            Log.d(TAG, "이름: " + label + "  사용량(Bytes): " + rxtxMobile.toString())
        }
    }

    fun setWeeklyDataUsageForAllApp(){
        for (info in packageInfoList) {
            val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                null,
                NOW - INTERVAL_WEEK,
                NOW,
                packageManager.getApplicationInfo(info.packageName, 0).uid
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
                    info.packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
            _weeklyDataUsageMap[label] = rxtxMobile
//            Log.d(TAG, "이름: " + label + "  사용량(Bytes): " + rxtxMobile.toString())
        }
    }

    fun setMonthlyDataUsageForAllApp(){
        for (info in packageInfoList) {
            val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,
                null,
                NOW - INTERVAL_MONTH,
                NOW,
                packageManager.getApplicationInfo(info.packageName, 0).uid
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
                    info.packageName,
                    PackageManager.GET_META_DATA
                )
            ).toString()
            _monthlyDataUsageMap[label] = rxtxMobile
        }
    }

    private fun getDailyDataUsageForAllApp(): Map<String, Long> {
        return dailyDataUsageMap
    }

    private fun getWeeklyDataUsageForAllApp(): Map<String, Long> {
        return weeklyDataUsageMap
    }

    private fun getMonthlyDataUsageForAllApp(): Map<String, Long> {
        return monthlyDataUsageMap
    }


}