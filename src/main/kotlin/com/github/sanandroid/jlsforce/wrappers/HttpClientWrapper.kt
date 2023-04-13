package com.github.sanandroid.jlsforce.wrappers

import java.net.http.HttpClient

interface HttpClientWrapper {
    private val client: HttpClient
        get() = HttpClient.newHttpClient()
}

class HttpClientWrapperImpl : HttpClientWrapper
