package preaterii.scanner.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import preaterii.scanner.MainActivity
import preaterii.scanner.R
import preaterii.scanner.ScannerPreview
import preaterii.scanner.ui.theme.ScannerTheme

class ResultActivity : ComponentActivity() {
    companion object {
        const val EXTRA_SCAN_DATA = "scanned"
    }

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

@Composable
private fun ResultScreen(
    contents: String,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val barcodeBitmap = remember(contents) {
        generateBarcodeBitmap(contents)
    }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShareClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = stringResource(id = R.string.share)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (barcodeBitmap != null) {
                BarcodeSection(contents = contents, barcodeBitmap = barcodeBitmap)
            }

            ScannedContentSection(contents = contents, context = context, uriHandler = uriHandler)

            ShoppingLinksSection(contents = contents, uriHandler = uriHandler)
        }
    }
}

@Composable
private fun BarcodeSection(
    contents: String,
    barcodeBitmap: Bitmap
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (contents.isEanOrISBN()) stringResource(id = R.string.barcode_ean13) else stringResource(id = R.string.qr_code),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = barcodeBitmap.asImageBitmap(),
                    contentDescription = stringResource(id = R.string.barcode),
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun ScannedContentSection(
    contents: String,
    context: Context,
    uriHandler: UriHandler
) {
    val isUrl = contents.isUrl()
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.scanned_content),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { copyToClipboard(context, contents) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(id = R.string.copy),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            SelectionContainer {
                Text(
                    text = contents,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isUrl) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isUrl) TextDecoration.Underline else null,
                    modifier = Modifier.then(
                        if (isUrl) {
                            Modifier.clickable {
                                val url = if (!contents.startsWith("http://", true) && !contents.startsWith("https://", true)) {
                                    "https://$contents"
                                } else {
                                    contents
                                }
                                uriHandler.openUri(url)
                            }
                        } else {
                            Modifier
                        }
                    )
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        context.resources.getString(R.string.scanned_result_clipboard),
        text
    )
    clipboard.setPrimaryClip(clip)
    Toast.makeText(
        context,
        context.resources.getString(R.string.copied_to_clipboard),
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
private fun ShoppingLinksSection(
    contents: String,
    uriHandler: UriHandler
) {
    if (contents.isEanOrISBN()) {
        val sites = getShoppingSites(contents, LocalConfiguration.current.locales[0])

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.search_online),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start)
            )

            sites.forEach { site ->
                ShoppingLinkCard(
                    name = site.name,
                    url = site.url,
                    uriHandler = uriHandler
                )
            }
        }
    }
}

@Composable
private fun ShoppingLinkCard(
    name: String,
    url: String,
    uriHandler: UriHandler
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(url) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(id = R.string.open),
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        }
    }
}

@ScannerPreview
@Composable
private fun ResultScreenParamPreview(
    @PreviewParameter(ResultScreenParameterProvider::class) contents: String
) {
    ScannerTheme {
        ResultScreen(
            contents = contents,
            onBackClick = {},
            onShareClick = {}
        )
    }
}
