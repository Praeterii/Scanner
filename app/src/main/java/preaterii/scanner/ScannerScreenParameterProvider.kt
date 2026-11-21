package preaterii.scanner

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ScannerScreenParameterProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}