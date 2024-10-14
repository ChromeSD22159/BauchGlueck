package de.frederikkohler.bauchglueck

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import util.BuildConfig
import util.KeyValueStorage

@RequiresApi(Build.VERSION_CODES.Q)
class AppOpenManager(private val myApp: Application) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

        private var appOpenAd : AppOpenAd? = null
        private var currentActivity : Activity? = null
        private var isShowingAd = false
        private val lastAdShowTimeKey = "lastAdShowTime"

        init {
            myApp.registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        }

        private fun wasLastAdShownMoreThanNHoursAgo(): Boolean {
            val lastLoading = KeyValueStorage(myApp).getLong(lastAdShowTimeKey, 0)
            logging().info { "Last Ads Shown: $lastLoading" }
            if (lastLoading == 0L) return true

            val numHours = 4
            val dateDifference: Long = Clock.System.now().toEpochMilliseconds() - lastLoading
            val numMilliSecondsPerHour: Long = 3600000
            val numMilliSecondsPerMinute: Long = 60000
            return dateDifference >= numMilliSecondsPerHour * numHours
        }

        private fun fetchAd() {
            if (appOpenAd != null) {
                return
            }

            val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    showAdIfAvailable()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        logging().error { "Fehler beim Laden der App-Open-Anzeige: ${loadAdError.message}" }
                    }
            }

            val request = AdRequest.Builder().build()
            AppOpenAd.load(
                myApp,
                BuildConfig.APP_START_AD_ID,
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                loadCallback
            )
        }

        private fun showAdIfAvailable(){
            if (!isShowingAd && appOpenAd != null){
                appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback(){
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        isShowingAd = false

                        if(!wasLastAdShownMoreThanNHoursAgo()) {
                            fetchAd()
                        }
                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                        val now = Clock.System.now().toEpochMilliseconds()
                        KeyValueStorage(myApp).putLong(lastAdShowTimeKey, now)
                    }
                }
                appOpenAd!!.show(currentActivity!!)
            } else {
                // Anzeige nicht verfügbar oder bereits angezeigt
                // Nichts tun
                if(!wasLastAdShownMoreThanNHoursAgo()) {
                    fetchAd()
                }
            }
        }

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            if (!isShowingAd){
                currentActivity = activity

                if (wasLastAdShownMoreThanNHoursAgo()) {
                    fetchAd()
                }
            }
        }

        override fun onActivityResumed(activity: Activity) {
            if (!isShowingAd){
                currentActivity = activity
            }
        }

        override fun onActivityPaused(p0: Activity) {}

        override fun onActivityStopped(p0: Activity) {}

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

        override fun onActivityDestroyed(p0: Activity) {
            currentActivity = null
        }

}