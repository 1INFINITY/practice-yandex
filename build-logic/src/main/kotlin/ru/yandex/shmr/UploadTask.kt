package ru.yandex.shmr

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class UploadTask : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:InputFile
    abstract val totalSizeFile: RegularFileProperty

    @get:Input
    abstract val apkName: Property<String>

    @TaskAction
    fun upload() {
        val api = TelegramApi(HttpClient(OkHttp))
        val token = ""
        val chatId = ""

        runBlocking {
            totalSizeFile.get().asFile
                .readText()
                .toLong()
                .also {
                    api.sendMessage("This file size is $it Mb", token, chatId)
                }

            apkDir.get().asFile.listFiles()
                ?.filter { it.name.endsWith(".apk") }
                ?.forEach {
                    println("FILE = ${it.absolutePath}")
                    api.uploadFile(it, apkName.get(), token, chatId)
                }

        }
    }
}
