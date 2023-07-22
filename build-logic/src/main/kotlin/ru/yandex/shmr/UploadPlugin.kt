package ru.yandex.shmr

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

open class UploadPluginExtension {
  var validateApkSizeEnabled: Boolean = true
  var maxApkSizeMb: Int = 100
}

class UploadPlugin : Plugin<Project> {

  override fun apply(project: Project) {

    val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
      ?: throw GradleException("'com.android.application' plugin required.")

    val extension = project.extensions.create("uploadConfig", UploadPluginExtension::class.java)

    androidComponents.onVariants { variant ->
      val capVariantName = variant.name.capitalize()
      val apkDirectory = variant.artifacts.get(SingleArtifact.APK)
      val android = project.extensions.getByType(AppExtension::class.java)
      val versionCode = android.defaultConfig.versionName
      val outputFileName = "todolist-$capVariantName-$versionCode.apk"

      val validateTask = project.tasks.register("validateApkSizeFor$capVariantName", ValidateApkSize::class.java) {
        apkDir.set(apkDirectory)
        validateEnabled.set(extension.validateApkSizeEnabled)
        maxApkSizeMb.set(extension.maxApkSizeMb)
      }

      project.tasks.register("uploadApkFor$capVariantName", UploadTask::class.java) {
        dependsOn("validateApkSizeFor$capVariantName")
        totalSizeFile.set(validateTask.get().totalSizeFile)
        apkDir.set(apkDirectory)
        apkName.set(outputFileName)
      }
    }
  }
}