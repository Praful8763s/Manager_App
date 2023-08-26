package app.revanced.manager.flutter

import android.os.Handler
import android.os.Looper
import app.revanced.manager.flutter.utils.Aapt
import app.revanced.manager.flutter.utils.aligning.ZipAligner
import app.revanced.manager.flutter.utils.signing.Signer
import app.revanced.manager.flutter.utils.zip.ZipFile
import app.revanced.manager.flutter.utils.zip.structures.ZipEntry
import app.revanced.patcher.PatchBundleLoader
import app.revanced.patcher.Patcher
import app.revanced.patcher.PatcherOptions
import app.revanced.patcher.extensions.PatchExtensions.compatiblePackages
import app.revanced.patcher.extensions.PatchExtensions.patchName
import app.revanced.patcher.patch.PatchResult
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

private const val PATCHER_CHANNEL = "app.revanced.manager.flutter/patcher"
private const val INSTALLER_CHANNEL = "app.revanced.manager.flutter/installer"

class MainActivity : FlutterActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var installerChannel: MethodChannel
    private var cancel: Boolean = false
    private var stopResult: MethodChannel.Result? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val mainChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, PATCHER_CHANNEL)
        installerChannel =
            MethodChannel(flutterEngine.dartExecutor.binaryMessenger, INSTALLER_CHANNEL)
        mainChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                "runPatcher" -> {
                    val patchBundleFilePath = call.argument<String>("patchBundleFilePath")
                    val originalFilePath = call.argument<String>("originalFilePath")
                    val inputFilePath = call.argument<String>("inputFilePath")
                    val patchedFilePath = call.argument<String>("patchedFilePath")
                    val outFilePath = call.argument<String>("outFilePath")
                    val integrationsPath = call.argument<String>("integrationsPath")
                    val selectedPatches = call.argument<List<String>>("selectedPatches")
                    val cacheDirPath = call.argument<String>("cacheDirPath")
                    val keyStoreFilePath = call.argument<String>("keyStoreFilePath")
                    val keystorePassword = call.argument<String>("keystorePassword")

                    if (patchBundleFilePath != null &&
                        originalFilePath != null &&
                        inputFilePath != null &&
                        patchedFilePath != null &&
                        outFilePath != null &&
                        integrationsPath != null &&
                        selectedPatches != null &&
                        cacheDirPath != null &&
                        keyStoreFilePath != null &&
                        keystorePassword != null
                    ) {
                        cancel = false
                        runPatcher(
                            result,
                            patchBundleFilePath,
                            originalFilePath,
                            inputFilePath,
                            patchedFilePath,
                            outFilePath,
                            integrationsPath,
                            selectedPatches,
                            cacheDirPath,
                            keyStoreFilePath,
                            keystorePassword
                        )
                    } else {
                        result.notImplemented()
                    }
                }

                "stopPatcher" -> {
                    cancel = true
                    stopResult = result
                }

                else -> result.notImplemented()
            }
        }
    }

    private fun runPatcher(
        result: MethodChannel.Result,
        patchBundleFilePath: String,
        originalFilePath: String,
        inputFilePath: String,
        patchedFilePath: String,
        outFilePath: String,
        integrationsPath: String,
        selectedPatches: List<String>,
        cacheDirPath: String,
        keyStoreFilePath: String,
        keystorePassword: String
    ) {
        val originalFile = File(originalFilePath)
        val inputFile = File(inputFilePath)
        val patchedFile = File(patchedFilePath)
        val outFile = File(outFilePath)
        val integrations = File(integrationsPath)
        val keyStoreFile = File(keyStoreFilePath)
        val cacheDir = File(cacheDirPath)

        Thread {
            try {
                Logger.getLogger("").apply {
                    handlers.forEach {
                        it.close()
                        removeHandler(it)
                    }
                    object : java.util.logging.Handler() {
                        override fun publish(record: LogRecord) = formatter.format(record).toByteArray().let {
                            if (record.level.intValue() > Level.INFO.intValue())
                                System.err.write(it)
                            else
                                System.out.write(it)
                        }

                        override fun flush() {
                            System.out.flush()
                            System.err.flush()
                        }

                        override fun close() = flush()
                    }.also {
                        it.level = Level.ALL
                        it.formatter = SimpleFormatter()
                    }.let(::addHandler)
                }
                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 0.1,
                            "header" to "",
                            "log" to "Copying original apk"
                        )
                    )
                }

                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }

                originalFile.copyTo(inputFile, true)

                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }

                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 0.2,
                            "header" to "Reading apk...",
                            "log" to "Reading input apk"
                        )
                    )
                }

                val patcher =
                    Patcher(
                        PatcherOptions(
                            inputFile,
                            cacheDir,
                            Aapt.binary(applicationContext).absolutePath,
                            cacheDir.path,
                        )
                    )

                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }

                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf("progress" to 0.3, "header" to "Loading patches...", "log" to "Loading patches")
                    )
                }

                val patches =
                    PatchBundleLoader.Dex(
                        File(patchBundleFilePath)
                    ).filter { patch ->
                        (patch.compatiblePackages?.any { it.name == patcher.context.packageMetadata.packageName } == true || patch.compatiblePackages.isNullOrEmpty()) &&
                                selectedPatches.any { it == patch.patchName }
                    }

                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }

                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 0.5,
                            "header" to "Executing patches...",
                            "log" to ""
                        )
                    )
                }

                patcher.apply {
                    acceptIntegrations(listOf(integrations))
                    acceptPatches(patches)

                    runBlocking {
                        apply(false).collect { patchResult: PatchResult ->
                            patchResult.exception?.let {
                                if (cancel) {
                                    handler.post { stopResult!!.success(null) }
                                    this.cancel()
                                    return@collect
                                }
                                StringWriter().use { writer ->
                                    it.printStackTrace(PrintWriter(writer))
                                    handler.post {
                                        installerChannel.invokeMethod(
                                            "update",
                                            mapOf("progress" to 0.5, "header" to "", "log" to "${patchResult.patchName} failed: $writer")
                                        )
                                    }
                                }
                            } ?: run {
                                if (cancel) {
                                    handler.post { stopResult!!.success(null) }
                                    this.cancel()
                                    return@collect
                                }
                                val msg = "${patchResult.patchName} succeeded"
                                handler.post {
                                    installerChannel.invokeMethod(
                                        "update",
                                        mapOf(
                                            "progress" to 0.5,
                                            "header" to "",
                                            "log" to msg
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }

                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 0.7,
                            "header" to "Repacking apk...",
                            "log" to ""
                        )
                    )
                }
                val res = patcher.get()
                patcher.close()
                ZipFile(patchedFile).use { file ->
                    res.dexFiles.forEach {
                        if (cancel) {
                            handler.post { stopResult!!.success(null) }
                            return@Thread
                        }
                        file.addEntryCompressData(
                            ZipEntry.createWithName(it.name),
                            it.stream.readBytes()
                        )
                    }
                    res.resourceFile?.let {
                        file.copyEntriesFromFileAligned(
                            ZipFile(it),
                            ZipAligner::getEntryAlignment
                        )
                    }
                    file.copyEntriesFromFileAligned(
                        ZipFile(inputFile),
                        ZipAligner::getEntryAlignment
                    )
                }
                if (cancel) {
                    handler.post { stopResult!!.success(null) }
                    return@Thread
                }
                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 0.9,
                            "header" to "Signing apk...",
                            "log" to ""
                        )
                    )
                }

                try {
                    Signer("ReVanced", keystorePassword).signApk(
                        patchedFile,
                        outFile,
                        keyStoreFile
                    )
                } catch (e: Exception) {
                    //log to console
                    print("Error signing apk: ${e.message}")
                    e.printStackTrace()
                }

                handler.post {
                    installerChannel.invokeMethod(
                        "update",
                        mapOf(
                            "progress" to 1.0,
                            "header" to "Finished!",
                            "log" to "Finished!"
                        )
                    )
                }
            } catch (ex: Throwable) {
                if (!cancel) {
                    val stack = ex.stackTraceToString()
                    handler.post {
                        installerChannel.invokeMethod(
                            "update",
                            mapOf(
                                "progress" to -100.0,
                                "header" to "Aborted...",
                                "log" to "An error occurred! Aborted\nError:\n$stack"
                            )
                        )
                    }
                }
            }
            handler.post { result.success(null) }
        }.start()
    }

//    inner class ManagerLogger : Logger {
//        override fun error(msg: String) {
//            handler.post {
//                installerChannel
//                    .invokeMethod(
//                        "update",
//                        mapOf("progress" to -1.0, "header" to "", "log" to msg)
//                    )
//            }
//        }
//
//        override fun warn(msg: String) {
//            handler.post {
//                installerChannel.invokeMethod(
//                    "update",
//                    mapOf("progress" to -1.0, "header" to "", "log" to msg)
//                )
//            }
//        }
//
//        override fun info(msg: String) {
//            handler.post {
//                installerChannel.invokeMethod(
//                    "update",
//                    mapOf("progress" to -1.0, "header" to "", "log" to msg)
//                )
//            }
//        }
//
//        override fun trace(_msg: String) { /* unused */
//        }
//    }
}
