package preaterii.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class MainActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var switchFlashlightButton: Button
    private lateinit var switchCameraButton: Button
    private var isFlashlightOn = false

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 0x42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_scanner)

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        switchFlashlightButton = findViewById(R.id.switch_flashlight)
        switchCameraButton = findViewById(R.id.switch_camera)

        if (!hasFlash()) {
            switchFlashlightButton.visibility = View.GONE
        }

        if (!hasMultipleCameras()) {
            switchCameraButton.visibility = View.GONE
        }

        switchFlashlightButton.setOnClickListener {
            switchFlashlight()
        }

        switchCameraButton.setOnClickListener {
            switchCamera()
        }

        barcodeScannerView.setStatusText("")

        barcodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    if (it.text != null) {
                        barcodeScannerView.pause()
                        val intent = Intent(this@MainActivity, ResultActivity::class.java)
                        intent.putExtra(ResultActivity.EXTRA_SCAN_DATA, it.text)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                // Do nothing
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.pause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                barcodeScannerView.resume()
            } else {
                Toast.makeText(this, "Camera permission is required to scan barcodes", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun hasMultipleCameras(): Boolean {
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
