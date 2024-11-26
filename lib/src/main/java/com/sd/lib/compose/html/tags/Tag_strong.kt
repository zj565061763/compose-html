package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.sd.lib.compose.html.FComposeHtml
import org.jsoup.nodes.Element

internal class Tag_strong : FComposeHtml.Tag() {
   override fun elementEnd(builder: AnnotatedString.Builder, element: Element, start: Int, end: Int) {
      builder.addStyle(
         style = SpanStyle(fontWeight = FontWeight.Bold),
         start = start,
         end = end,
      )
   }
}