package sk.stu.fei.mobv.network.helpers

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import sk.stu.fei.mobv.helpers.Config
import sk.stu.fei.mobv.helpers.PreferenceData

class AuthInterceptor(val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        synchronized(this) {
            val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", "Mobv-Android/1.0.0")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")

            if (chain.request().header("mobv-auth")?.compareTo("accept") == 0) {
                request.addHeader(
                    "Authorization",
                    "Bearer ${PreferenceData.getInstance().getUserItem(context)?.access}"
                )

            }
            PreferenceData.getInstance().getUserItem(context)?.id?.let {
                request.addHeader(
                    "x-user",
                    it
                )
            }
            request.addHeader("x-apikey", Config.API_KEY)

            return chain.proceed(request.build())
        }
    }

}