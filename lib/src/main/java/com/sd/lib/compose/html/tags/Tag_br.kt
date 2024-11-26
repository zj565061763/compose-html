package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import com.sd.lib.compose.html.FComposeHtml
import org.jsoup.nodes.Element

internal class Tag_br : FComposeHtml.Tag() {
   override fun elementStart(builder: AnnotatedString.Builder, element: Element) {
      builder.appendLine()
   }
}