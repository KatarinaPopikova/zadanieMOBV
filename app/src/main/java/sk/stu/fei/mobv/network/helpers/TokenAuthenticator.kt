package sk.stu.fei.mobv.network.helpers

import android.content.Context
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.network.RestApiService
import sk.stu.fei.mobv.network.UserRefreshBody
import sk.stu.fei.mobv.network.dtos.asDomainModel

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
        synchronized(this) {
            if (response.request().header("mobv-auth")
                    ?.compareTo("accept") == 0 && response.code() == 401
            ) {
                val user = PreferenceData.getInstance().getUserItem(context)

                if (user == null) {
                    PreferenceData.getInstance().clearData(context)
                    return null
                }

                val tokenResponse = RestApiService.create(context).refreshUser(
                    UserRefreshBody(
                        user.refresh
                    )
                ).execute()

                if (tokenResponse.isSuccessful) {
                    tokenResponse.body()?.let {
                        PreferenceData.getInstance().putUserItem(context, it.asDomainModel())
                        return response.request().newBuilder()
                            .header("authorization", "Bearer ${it.access}")
                            .build()
                    }
                }

                PreferenceData.getInstance().clearData(context)
                return null


            }
        }
        return null
    }
}