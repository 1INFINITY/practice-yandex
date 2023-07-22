package ru.yandex.shmr

import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class ValidateApkSize : DefaultTask() {

    @get:InputDirectory
    abstract val apkDir: DirectoryProperty

    @get:OutputFile
    abstract val totalSizeFile: RegularFileProperty

    @get:Input
    abstract val validateEnabled: Property<Boolean>

    @get:Input
    abstract val maxApkSizeMb: Property<Int>

    init {
        totalSizeFile.convention(project.layout.buildDirectory.file("total-size-file.txt"))
    }
    @TaskAction
    fun validate() {
        runBlocking {
            val totalBytes =
                apkDir.get().asFile.listFiles()
                    ?.filter { it.name.endsWith(".apk") }
                    ?.map { it.length() }
                    ?.reduce { sum, apkSize -> sum + apkSize }
                    ?: throw GradleException("APK files not found!")

            val totalMb = totalBytes / bytesInMegabyte

            if(totalMb > maxApkSizeMb.get() && validateEnabled.get())
                throw GradleException("APK size is not valid. Aborting task.")

            totalSizeFile.get().asFile.writeText(totalMb.toString())
        }
    }
    companion object {
        const val defaultValidSizeMb = 100
        const val bytesInMegabyte = 1024 * 1024
    }
}