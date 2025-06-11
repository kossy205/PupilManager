package com.kosiso.pupilmanager.data.remote

import okhttp3.Interceptor
import java.util.UUID

class Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("X-Request-ID",UUID.randomUUID().toString())
            .addHeader("User-Agent", "Bridge Android Tech Test")
            .build()

        return chain.proceed(newRequest)
    }
}
