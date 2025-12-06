package preaterii.scanner

import androidx.core.text.isDigitsOnly

fun String.isEanOrISBN(): Boolean {
    return this.length == 13 && this.isDigitsOnly()
}