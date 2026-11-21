package preaterii.scanner.result

import java.util.Locale

internal fun getShoppingSites(contents: String, locale: Locale = Locale.getDefault()): List<ShoppingSite> {
    val language = locale.language.lowercase()
    val country = locale.country.uppercase()

    return when {
        country == "PL" || language == "pl" -> listOf(
            ShoppingSite("Ceneo", "https://www.ceneo.pl/;szukaj-$contents"),
            ShoppingSite("Amazon.pl", "https://www.amazon.pl/s?k=$contents"),
            ShoppingSite("Allegro", "https://allegro.pl/listing?string=$contents&order=p")
        )
        country == "DE" || language == "de" -> listOf(
            ShoppingSite("Amazon.de", "https://www.amazon.de/s?k=$contents")
        )
        country == "GB" || country == "UK" -> listOf(
            ShoppingSite("Amazon.co.uk", "https://www.amazon.co.uk/s?k=$contents")
        )
        country == "FR" || language == "fr" -> listOf(
            ShoppingSite("Amazon.fr", "https://www.amazon.fr/s?k=$contents")
        )
        country == "ES" || language == "es" -> listOf(
            ShoppingSite("Amazon.es", "https://www.amazon.es/s?k=$contents")
        )
        country == "IT" || language == "it" -> listOf(
            ShoppingSite("Amazon.it", "https://www.amazon.it/s?k=$contents")
        )
        country == "JP" || language == "ja" -> listOf(
            ShoppingSite("Amazon.co.jp", "https://www.amazon.co.jp/s?k=$contents")
        )
        country == "CA" -> listOf(
            ShoppingSite("Amazon.ca", "https://www.amazon.ca/s?k=$contents")
        )
        country == "IN" -> listOf(
            ShoppingSite("Amazon.in", "https://www.amazon.in/s?k=$contents")
        )
        else -> listOf(
            ShoppingSite("Amazon.com", "https://www.amazon.com/s?k=$contents")
        )
    }
}
