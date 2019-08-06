package io.golos.cyber_android.application

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import io.golos.cyber_android.application.dependency_injection.DependencyInjectionStorage
import io.golos.cyber_android.application.dependency_injection.graph.app.AppComponent
import io.golos.cyber_android.fcm.CommunFirebaseMessagingService
import io.golos.domain.Logger
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class App : Application() {
    @Inject
    internal lateinit var appCore: AppCore

    @Inject
    internal lateinit var logger: Logger

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var injections : DependencyInjectionStorage
            private set

        lateinit var log: Logger
            private set
    }

    override fun onCreate() {
        super.onCreate()

        injections = DependencyInjectionStorage(applicationContext)
        injections.get<AppComponent>().inject(this)

        log = logger

        appCore.initialize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CommunFirebaseMessagingService.createChannels(this)
        }
    }
}