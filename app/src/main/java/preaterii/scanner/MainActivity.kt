package preaterii.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Camera permission is required to scan barcodes",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScannerScreen(
                hasFlash = hasFlash(),
                hasMultipleCameras = hasMultipleCameras(),
                onScanResult = { text ->
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_SCAN_DATA, text)
                    startActivity(intent)
                    finish()
                },
                onRequestCameraPermission = {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            )
        }
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun hasMultipleCameras(): Boolean {
        return applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }
}

@Composable
private fun ScannerScreen(
    hasFlash: Boolean,
    hasMultipleCameras: Boolean,
    onScanResult: (String) -> Unit,
    onRequestCameraPermission: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isFlashlightOn by remember { mutableStateOf(false) }

    // State to hold the barcode view reference so we can control it
    var barcodeView: DecoratedBarcodeView? by remember { mutableStateOf(null) }
    val isInPreview = LocalInspectionMode.current

    // Check permission on start
    LaunchedEffect(Unit) {
        if (!isInPreview && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onRequestCameraPermission()
        }
    }

    // Handle Lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                barcodeView?.resume()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                barcodeView?.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isInPreview) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    DecoratedBarcodeView(ctx).apply {
                        setStatusText("")
                        decodeContinuous(object : BarcodeCallback {
                            override fun barcodeResult(result: BarcodeResult?) {
                                result?.let {
                                    if (it.text != null) {
                                        pause()
                                        onScanResult(it.text)
                                    }
                                }
                            }

                            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                        })
                        barcodeView = this
                    }
                }
            )
        } else {
            Text(
                text = "Camera Preview",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (hasFlash) {
                Button(
                    onClick = {
                        barcodeView?.let { view ->
                            if (isFlashlightOn) {
                                view.setTorchOff()
                                isFlashlightOn = false
                            } else {
                                view.setTorchOn()
                                isFlashlightOn = true
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.flashlight))
                }
            }

            if (hasMultipleCameras) {
                Button(
                    onClick = {
                        barcodeView?.let { view ->
                            if (isFlashlightOn) {
                                isFlashlightOn = false
                                view.setTorchOff()
                            }

                            view.pause()
                            val settings = view.barcodeView.cameraSettings
                            // Toggle between 0 (back) and 1 (front)
                            settings.requestedCameraId =
                                if (settings.requestedCameraId == 1) 0 else 1
                            view.barcodeView.cameraSettings = settings
                            view.resume()
                        }
                    },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.switch_camera))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannerScreenPreview() {
    ScannerScreen(
        hasFlash = true,
        hasMultipleCameras = true,
        onScanResult = {},
        onRequestCameraPermission = {}
    )
}
