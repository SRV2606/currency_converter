package com.example.domain.models

import com.example.domain.domain.models.ApiError


sealed class ClientResult<out T> {
    data class Success<out T>(val data: T) : ClientResult<T>()
    data class Error(val error: ApiError) : ClientResult<Nothing>()
    object InProgress : ClientResult<Nothing>()
}
