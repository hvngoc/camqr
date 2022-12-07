package com.q.r.cam.bar.lib

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Created by hvngoc on 12/7/22
 */
@androidx.camera.core.ExperimentalGetImage
class BarCodeAnalyzer(val onSuccess: (List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image ?: return

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        val barcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                Log.i("con heo", " heo bar OKKK")
                onSuccess(barcodes)
            }
            .addOnFailureListener {
                Log.i("con heo", " heo bar Failure $it")
            }.addOnCompleteListener {
                mediaImage.close()
                imageProxy.close()
            }
    }
}