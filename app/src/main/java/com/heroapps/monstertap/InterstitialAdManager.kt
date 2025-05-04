package com.heroapps.monstertap

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay

const val FULL_PAGE_UNIT_ID = "ca-app-pub-4989116616189159/5210921467"

/**
 * A manager class for loading and showing interstitial ads.
 */
class InterstitialAdManager(private val context: Context) {
    
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    
    // Callbacks for different ad events
    var onAdDismissed: (() -> Unit)? = null
    var onAdFailedToLoad: ((LoadAdError) -> Unit)? = null
    var onAdLoaded: (() -> Unit)? = null
    var onAdFailedToShow: ((AdError) -> Unit)? = null
    
    /**
     * Loads an interstitial ad.
     * @param adUnitId The ad unit ID for the interstitial ad.
     */
    fun loadAd(adUnitId: String) {
        if (interstitialAd != null || isLoading) {
            return  // Ad already loaded or loading
        }
        
        isLoading = true
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the error
                    isLoading = false
                    interstitialAd = null
                    onAdFailedToLoad?.invoke(adError)
                }
                
                override fun onAdLoaded(ad: InterstitialAd) {
                    // The ad was loaded successfully
                    isLoading = false
                    interstitialAd = ad
                    setupFullScreenContentCallback()
                    onAdLoaded?.invoke()
                }
            }
        )
    }
    
    /**
     * Shows the loaded interstitial ad.
     * @param activity The activity where the ad will be shown.
     * @return True if the ad was shown, false otherwise.
     */
    fun showAd(activity: Activity): Boolean {
        val ad = interstitialAd ?: return false
        
        ad.show(activity)
        return true
    }
    
    /**
     * Checks if an interstitial ad is loaded and ready to be shown.
     * @return True if an ad is loaded and ready, false otherwise.
     */
    fun isAdLoaded(): Boolean {
        return interstitialAd != null
    }
    
    /**
     * Sets up callbacks for full-screen content events.
     */
    private fun setupFullScreenContentCallback() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when the ad is dismissed
                interstitialAd = null
                onAdDismissed?.invoke()
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when the ad fails to show
                interstitialAd = null
                onAdFailedToShow?.invoke(adError)
            }
        }
    }
    
    /**
     * Releases resources when no longer needed.
     */
    fun release() {
        interstitialAd = null
        onAdDismissed = null
        onAdFailedToLoad = null
        onAdLoaded = null
        onAdFailedToShow = null
    }
}

/**
 * A composable function that remembers an InterstitialAdManager instance.
 * @param adUnitId The ad unit ID for the interstitial ad.
 * @param preload Whether to preload the ad when the composable is first created.
 * @param onAdDismissed Callback for when the ad is dismissed.
 * @param onAdFailedToLoad Callback for when the ad fails to load.
 * @param onAdLoaded Callback for when the ad is loaded.
 * @param onAdFailedToShow Callback for when the ad fails to show.
 * @return An InterstitialAdManager instance.
 */
@Composable
fun rememberInterstitialAdManager(
    adUnitId: String,
    preload: Boolean = true,
    onAdDismissed: (() -> Unit)? = null,
    onAdFailedToLoad: ((LoadAdError) -> Unit)? = null,
    onAdLoaded: (() -> Unit)? = null,
    onAdFailedToShow: ((AdError) -> Unit)? = null
): InterstitialAdManager {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val adManager = remember { InterstitialAdManager(context) }
    
    // Set callbacks
    DisposableEffect(adManager) {
        adManager.onAdDismissed = onAdDismissed
        adManager.onAdFailedToLoad = onAdFailedToLoad
        adManager.onAdLoaded = onAdLoaded
        adManager.onAdFailedToShow = onAdFailedToShow
        
        // Load the ad if preload is true
        if (preload) {
            adManager.loadAd(adUnitId)
        }
        
        // Clean up when the composable is disposed
        onDispose {
            adManager.release()
        }
    }
    
    // Handle lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                adManager.release()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    return adManager
}

@Composable
fun LaunchInterstitialAds(context: Context) {
    val interstitialAdManager = rememberInterstitialAdManager(
        adUnitId = FULL_PAGE_UNIT_ID,
        preload = true,
        onAdDismissed = {
            // Ad was dismissed, continue with the game
            println("Interstitial ad was dismissed")
            //adShownThisSession = true
        },
        onAdFailedToLoad = { loadError: LoadAdError ->
            // Ad failed to load, handle the error
            println("Failed to load interstitial ad: ${loadError.message}")
        },
        onAdLoaded = {
            // Ad loaded successfully
            println("Interstitial ad loaded successfully")
        },
        onAdFailedToShow = { showError: AdError ->
            // Ad failed to show, handle the error
            println("Failed to show interstitial ad: ${showError.message}")
        }
    )

    LaunchedEffect(Unit) {
        delay(1000)
        val activity = context as? Activity
        activity?.let {
            if (interstitialAdManager.isAdLoaded()) {
                interstitialAdManager.showAd(it)
            } else {
                // Ad not loaded, try to load it
                interstitialAdManager.loadAd(FULL_PAGE_UNIT_ID)
            }
        }
    }
}