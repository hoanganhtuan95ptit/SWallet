package com.simple.wallet.utils.exts

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}

@Composable
fun TextFormatter(text: String): AnnotatedString {

    val tokens = symbolPattern.findAll(text)

    return buildAnnotatedString {

        var cursorPosition = 0

//        val codeSnippetBackground =
//            if (primary) {
//                MaterialTheme.colorScheme.secondary
//            } else {
//                MaterialTheme.colorScheme.surface
//            }

        for (token in tokens) {

            append(text.slice(cursorPosition until token.range.first))

            val (annotatedString, stringAnnotation) = getSymbolAnnotation(
                matchResult = token,
                colorScheme = MaterialTheme.colorScheme,
//                primary = primary,
//                codeSnippetBackground = codeSnippetBackground
            )
            append(annotatedString)

            if (stringAnnotation != null) {
                val (item, start, end, tag) = stringAnnotation
                addStringAnnotation(tag = tag, start = start, end = end, annotation = item)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }
}

private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
//    primary: Boolean,
//    codeSnippetBackground: Color
): SymbolAnnotation {

    return when (matchResult.value.first()) {
        '@' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
//                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ),
            StringAnnotation(
                item = matchResult.value.substring(1),
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.PERSON.name
            )
        )

        '*' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('*'),
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
            ),
            null
        )

        '_' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('_'),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic)
            ),
            null
        )

        '~' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('~'),
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
            ),
            null
        )

        '`' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value.trim('`'),
                spanStyle = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
//                    background = codeSnippetBackground,
                    baselineShift = BaselineShift(0.2f)
                )
            ),
            null
        )

        'h' -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value,
                spanStyle = SpanStyle(
//                    color = if (primary) colorScheme.inversePrimary else colorScheme.primary
                )
            ),
            StringAnnotation(
                item = matchResult.value,
                start = matchResult.range.first,
                end = matchResult.range.last,
                tag = SymbolAnnotationType.LINK.name
            )
        )

        else -> SymbolAnnotation(
            AnnotatedString(
                text = matchResult.value
            ),
            null
        )
    }
}

enum class SymbolAnnotationType {
    PERSON, LINK
}

typealias StringAnnotation = AnnotatedString.Range<String>

typealias SymbolAnnotation = Pair<AnnotatedString, StringAnnotation?>