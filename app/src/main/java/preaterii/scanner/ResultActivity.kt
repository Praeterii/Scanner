package preaterii.scanner

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import preaterii.scanner.ui.theme.ScannerTheme

class ResultActivity : ComponentActivity() {
    companion object {
        const val EXTRA_SCAN_DATA = "scanned"
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contents = intent?.extras?.getString(EXTRA_SCAN_DATA)

        if (contents != null) {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            })

            setContent {
                ScannerTheme {
                    ResultScreen(
                        contents = contents,
                        onBackClick = { navigateBack() },
                        onShareClick = { shareContent(contents) }
                    )
                }
            }
        } else {
            finish()
        }
    }

    private fun navigateBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun shareContent(contents: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, contents)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreen(
    contents: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val isPreview = LocalInspectionMode.current
    val barcodeBitmap = remember(contents) {
        if (isPreview) {
            createBitmap(600, 300).apply { eraseColor(android.graphics.Color.GRAY) }
        } else {
            generateBarcodeBitmap(contents)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.colorPrimary),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(id = R.string.share)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (barcodeBitmap != null) {
                    Image(
                        bitmap = barcodeBitmap.asImageBitmap(),
                        contentDescription = "Barcode",
                        modifier = Modifier
                    )
                }

                Text(
                    text = contents,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (contents.isEanOrISBN()) {
                    WebUrl(url = "https://www.ceneo.pl/szukaj-$contents", uriHandler = uriHandler)
                    WebUrl(url = "https://www.amazon.pl/s?k=$contents", uriHandler = uriHandler)
                    WebUrl(url = "https://allegro.pl/listing?string=$contents&order=p", uriHandler = uriHandler)
                }
            }
        }
    }
}

@Composable
private fun WebUrl(
    url: String,
    uriHandler: UriHandler
) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append(url)
        }
        pop()
    }

    Text(
        text = annotatedString,
        textAlign = TextAlign.Center,
        modifier = Modifier.clickable {
            uriHandler.openUri(url)
        },
    )
}

private fun generateBarcodeBitmap(code: String): Bitmap? {
    val multiFormatWriter = MultiFormatWriter()
    return try {
        val type = if (code.isEanOrISBN()) {
            BarcodeFormat.EAN_13
        } else {
            BarcodeFormat.QR_CODE
        }
        // We can use a default size here, scaling is handled by Image composable
        val bitMatrix = multiFormatWriter.encode(code, type, 600, 300) 
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Preview(showBackground = true, device = TABLET, name = "Tablet")
@Composable
private fun ResultScreenPreview() {
    ScannerTheme {
        ResultScreen(
            contents = "5901588016443",
            onBackClick = {},
            onShareClick = {}
        )
    }
}
