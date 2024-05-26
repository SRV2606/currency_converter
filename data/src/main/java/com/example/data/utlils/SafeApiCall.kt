package com.example.data.data.utlils

import android.util.Log
import com.example.domain.domain.models.ApiError
import com.example.domain.domain.models.NoConnectivityException
import com.example.domain.models.ClientResult
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


private const val ERROR_TOKEN_EXPIRED = 403
private const val ERROR_TOKEN_NOT_PROVIDED = 402


suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ClientResult<T> {
    return try {
        val response = call.invoke()
        Log.d("SHAW_TAG", "safeApiCall: " + response)
        return if (response.isSuccessful) {
            response.body()?.let {
                createSuccess(it).apply {

                }
            } ?: run {
                createErrorWithResponse(response)
            }
        } else {
            createErrorWithResponse(response)
        }
    } catch (exception: Throwable) {
        createError(exception)
    }
}

fun <T> createSuccess(response: T): ClientResult<T> =
    ClientResult.Success(response)


private fun <T> createErrorWithResponse(response: Response<T>): ClientResult<T> {
    when (response.code()) {
        ERROR_TOKEN_EXPIRED -> {
            fireTokenExpiredEvent(response.code())
        }

        ERROR_TOKEN_NOT_PROVIDED -> {
            fireTokenExpiredEvent(response.code())
        }
    }
    return try {
        val error: ApiErrorResponse = Gson().fromJson(
            response.errorBody()?.string(),
            ApiErrorResponse::class.java
        )
        ClientResult.Error(ApiError(error.errorMessage.errorMessage))
    } catch (t: Throwable) {
        createError(t)
    }
}

private fun fireTokenExpiredEvent(code: Int) {
    ClientResult.Error(ApiError("Error With Code$code"))
}

private fun <T> createError(t: Throwable): ClientResult<T> {
    val defaultMessage = "Internet not working properly"
//        Timber.e(t)
//        FirebaseCrashlytics.getInstance().recordException(t)
    val error = when (t) {
        is NoConnectivityException -> {
            ApiError(defaultMessage)
        }

        is SSLHandshakeException -> {
            ApiError(defaultMessage)
        }

        is UnknownHostException -> {
            ApiError(defaultMessage)
        }

        else -> ApiError(t.localizedMessage)
    }
    return ClientResult.Error(error)
}


internal data class ApiErrorResponse(
    @SerializedName("auth")
    val auth: Boolean = false,
    @SerializedName("error")
    val errorMessage: ErrorMessage = ErrorMessage()
)

data class ErrorMessage(
    @SerializedName("name")
    val errorName: String = "",
    @SerializedName("message")
    val errorMessage: String = ""
)


