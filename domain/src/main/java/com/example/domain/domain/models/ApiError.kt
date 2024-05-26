package com.example.domain.domain.models

class ApiError(message: String) : Exception(message) {

}

class NoConnectivityException(message: String) : Exception(message) {

}
