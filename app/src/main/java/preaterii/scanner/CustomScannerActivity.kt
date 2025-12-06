package preaterii.scanner

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomScannerActivity : Activity() {
    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var switchFlashlightButton: Button
    private lateinit var switchCameraButton: Button
    private var isFlashlightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        switchFlashlightButton = findViewById(R.id.switch_flashlight)
        switchCameraButton = findViewById(R.id.switch_camera)

        if (!hasFlash()) {
            switchFlashlightButton.visibility = View.GONE
        }
        
        // Check if we have more than one camera, otherwise hide switch button
        if (!hasMultipleCameras()) {
            switchCameraButton.visibility = View.GONE
        }

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()

        switchFlashlightButton.setOnClickListener {
            switchFlashlight()
        }

        switchCameraButton.setOnClickListener {
            switchCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
    
    private fun hasMultipleCameras(): Boolean {
        // Simple check if device has a front camera feature might not be enough as we need count.
        // But for simplicity, we can assume if FEATURE_CAMERA_FRONT exists, we can switch.
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }

    private fun switchFlashlight() {
        if (isFlashlightOn) {
            barcodeScannerView.setTorchOff()
            isFlashlightOn = false
        } else {
            barcodeScannerView.setTorchOn()
            isFlashlightOn = true
        }
    }

    private fun switchCamera() {
        if (isFlashlightOn) {
            isFlashlightOn = false
            barcodeScannerView.setTorchOff()
        }
        
        barcodeScannerView.pause()
        val settings = barcodeScannerView.barcodeView.cameraSettings
        
        // Toggle between 0 (back) and 1 (front)
        if (settings.requestedCameraId == 1) {
            settings.requestedCameraId = 0
        } else {
            settings.requestedCameraId = 1
        }
        
        barcodeScannerView.barcodeView.cameraSettings = settings
        barcodeScannerView.resume()
    }
}
