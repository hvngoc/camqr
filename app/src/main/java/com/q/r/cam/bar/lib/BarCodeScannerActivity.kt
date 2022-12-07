package com.q.r.cam.bar.lib

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.q.r.cam.bar.R
import com.q.r.cam.bar.databinding.ActivityBarCodeScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by hvngoc on 12/7/22
 */
class BarCodeScannerActivity : AppCompatActivity() {

    companion object {
        const val RESULT_BARCODE_RAW_VALUE = "barcode_raw_value"
    }

    private lateinit var viewBindings: ActivityBarCodeScannerBinding
    private lateinit var cameraExecutorService: ExecutorService

    @androidx.camera.core.ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBindings = DataBindingUtil.setContentView(this, R.layout.activity_bar_code_scanner)

        cameraExecutorService = Executors.newSingleThreadExecutor()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutorService.shutdown()
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewBindings.viewFinder.surfaceProvider)
                    }

                val barcodeAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutorService,
                            BarCodeAnalyzer { barcodes ->
                                if (barcodes.isNotEmpty()) {

                                    val intent = Intent().apply {
                                        putExtra(
                                            RESULT_BARCODE_RAW_VALUE,
                                            barcodes.first().rawValue
                                        )
                                    }

                                    setResult(RESULT_OK, intent)

                                    cameraExecutorService.shutdown()

                                    finish()
                                }
                            }
                        )
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        barcodeAnalyzer
                    )

                } catch (e: Exception) {
                    Log.w("con heo ", "heo heo = $e")
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }
}