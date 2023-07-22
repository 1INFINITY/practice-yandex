package ru.yandex.shmr

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File

private const val BASE_URL = "https://api.telegram.org"

class TelegramApi(
    private val httpClient: HttpClient
) {

    suspend fun uploadFile(file: File, fileName: String, token: String, chatId: String) {
        val response = httpClient.post("$BASE_URL/bot${token}/sendDocument") {
            parameter("chat_id", chatId)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("document", file.readBytes(), Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "${ContentDisposition.Parameters.FileName}=\"${fileName}\""
                            )
                        })
                    }
                )
            )
        }
        println(response.status)
    }

    suspend fun sendMessage(message: String, token: String, chatId: String) {
        val response = httpClient.post("$BASE_URL/bot${token}/sendMessage") {
            parameter("chat_id", chatId)
            parameter("text", message)
        }
        println(response.status)
    }
}