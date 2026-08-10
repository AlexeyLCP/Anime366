package su.afk.yummy.tv.core.utils

private val PARAGRAPH_SEPARATOR_REGEX = Regex("\n\\s*\n")

/** Splits text into paragraphs on blank lines; returns the whole (trimmed) text as a single paragraph if there are none. */
fun String.toParagraphs(): List<String> =
    split(PARAGRAPH_SEPARATOR_REGEX).map { it.trim() }.filter { it.isNotBlank() }
        .ifEmpty { listOf(this) }
