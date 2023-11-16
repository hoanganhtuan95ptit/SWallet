package com.simple.wallet.utils.exts

import android.util.Log
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

val symbolPattern by lazy {
    Regex("""(https?://[^\s\t\n]+)|(`[^`]+`)|(@\w+)|(\*[\w]+\*)|(_[\w]+_)|(~[\w]+~)""")
}


@Composable
fun TextFormat(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {

    val textWrap = TextFormatter(text = text)

//    data class TextPaddingValues(
//        val horizontal: TextUnit = 0.sp,
//        val vertical: TextUnit = 0.sp
//    )
//
//    val cornerRadius = CornerRadius(0.toPx().toFloat(), 0.toPx().toFloat())
//    val padding = TextPaddingValues(horizontal = 0.sp, vertical = 0.sp)
//    val topMargin: TextUnit = 0.sp
//    val bottomMargin: TextUnit = 0.sp
//
//    val a = remember {
//        mutableStateOf(emptyList<Pair<StringAnnotation, List<androidx.compose.ui.geometry.Rect>>>())
//    }
//    Log.d("tuanha", "TextFormat: ")
//
//    textWrap.first.get
    Text(
        text = textWrap.first,
        style = style,
        modifier = modifier,
//            .drawBehind {
//
//            val path = Path()
//
//            a.value.forEach {
//
//                Log.d("tuanha", "TextFormat: ------>")
//                val boxes = it.second
//                boxes.forEachIndexed { index, box ->
//
//                Log.d("tuanha", "TextFormat: $index $box")
//
//                    path.asAndroidPath().rewind()
//                    path.addRoundRect(
//                        RoundRect(
//                            rect = box.copy(
//                                left = box.left - padding.horizontal.toPx(),
//                                right = box.right + padding.horizontal.toPx(),
//                                top = box.top - padding.vertical.toPx() + topMargin.toPx(),
//                                bottom = box.bottom + padding.vertical.toPx() - bottomMargin.toPx(),
//                            ),
//                            topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
//                            bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
//                            topRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero,
//                            bottomRight = if (index == boxes.lastIndex) cornerRadius else CornerRadius.Zero
//                        )
//                    )
////                    drawPath(
////                        path = path,
////                        color = backgroundColor,
////                        style = Fill
////                    )
////                    if (stroke != null) {
//                        drawPath(
//                            path = path,
//                            color = Color.Black,
//                            style = Stroke(
//                                width = 1.toPx().toFloat(),
//                            )
//                        )
////                    }
//                }
//            }
//        },
        textAlign = textAlign,
//        onTextLayout = { layoutResult ->
//
//            a.value = textWrap.second.map { Pair(it, layoutResult.getBoundingBoxes(it.start, it.end, true)) }
//        }
    )
}

fun TextLayoutResult.getBoundingBoxes(
    startOffset: Int,
    endOffset: Int,
    flattenForFullParagraphs: Boolean = false
): List<androidx.compose.ui.geometry.Rect> {

    if (startOffset == endOffset) {
        return emptyList()
    }

    val startLineNum = getLineForOffset(startOffset)
    val endLineNum = getLineForOffset(endOffset)

    if (flattenForFullParagraphs) {
        val isFullParagraph = (startLineNum != endLineNum)
                && getLineStart(startLineNum) == startOffset
                && multiParagraph.getLineEnd(endLineNum, visibleEnd = true) == endOffset

        if (isFullParagraph) {
            return listOf(
                Rect(
                    top = getLineTop(startLineNum),
                    bottom = getLineBottom(endLineNum),
                    left = 0f,
                    right = size.width.toFloat()
                )
            )
        }
    }

    // Compose UI does not offer any API for reading paragraph direction for an entire line.
    // So this code assumes that all paragraphs in the text will have the same direction.
    // It also assumes that this paragraph does not contain bi-directional text.
    val isLtr = multiParagraph.getParagraphDirection(offset = layoutInput.text.lastIndex) == ResolvedTextDirection.Ltr

    return fastMapRange(startLineNum, endLineNum) { lineNum ->

        androidx.compose.ui.geometry.Rect(
            top = getLineTop(lineNum),
            bottom = getLineBottom(lineNum),
            left = if (lineNum == startLineNum) {
                getHorizontalPosition(startOffset, usePrimaryDirection = isLtr)
            } else {
                getLineLeft(lineNum)
            },
            right = if (lineNum == endLineNum) {
                getHorizontalPosition(endOffset, usePrimaryDirection = isLtr)
            } else {
                getLineRight(lineNum)
            }
        )
    }
}

internal inline fun <R> fastMapRange(
    start: Int,
    end: Int,
    transform: (Int) -> R
): List<R> {
//    contract { callsInPlace(transform) }
    val destination = ArrayList<R>(/* initialCapacity = */ end - start + 1)
    for (i in start..end) {
        destination.add(transform(i))
    }
    return destination
}


@Composable
fun TextFormatter(text: String): Pair<AnnotatedString, List<StringAnnotation>> {

    val tokens = symbolPattern.findAll(text)

    val stringAnnotations = arrayListOf<StringAnnotation>()

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

                stringAnnotations.add(stringAnnotation)
            }

            cursorPosition = token.range.last + 1
        }

        if (!tokens.none()) {
            append(text.slice(cursorPosition..text.lastIndex))
        } else {
            append(text)
        }
    }.let {

        Pair(it, stringAnnotations)
    }
}


private fun getSymbolAnnotation(
    matchResult: MatchResult,
    colorScheme: ColorScheme,
//    primary: Boolean,
//    codeSnippetBackground: Color
): SymbolAnnotation {

    Log.d("tuanha", "getSymbolAnnotation: ${matchResult.value.first()}")

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
                    color = colorScheme.primary,
                )
            ),
            StringAnnotation(
                item = matchResult.value.trim('`'),
                start = matchResult.range.first,
                end = matchResult.range.last - 1,
                tag = SymbolAnnotationType.PERSON.name
            )
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