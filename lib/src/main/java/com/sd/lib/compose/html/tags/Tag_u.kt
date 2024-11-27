package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.sd.lib.compose.html.ComposeHtml
import org.jsoup.nodes.Element

internal class Tag_u : ComposeHtml.Tag() {
   override fun elementEnd(builder: AnnotatedString.Builder, element: Element, start: Int, end: Int) {
      builder.addStyle(
         style = SpanStyle(textDecoration = TextDecoration.Underline),
         start = start,
         end = end,
      )
   }
}