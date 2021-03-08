package grch.assignment.quotes.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class AppBackgroundListener : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        Timber.d("App start event")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        Timber.d("App stop event")
    }
}