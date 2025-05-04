package com.heroapps.monstertap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.MobileAds
import com.heroapps.monstertap.ui.theme.MonsterTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this) {}

        setContent {
            MonsterTapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameNavigation()
                }
            }
        }
    }
}

