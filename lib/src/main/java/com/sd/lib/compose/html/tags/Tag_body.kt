package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import com.sd.lib.compose.html.ComposeHtml
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

internal class Tag_body : ComposeHtml.Tag() {
   override fun elementText(builder: AnnotatedString.Builder, element: Element, textNode: TextNode) {
      if (textNode.wholeText == "\n") {
         // Ignore
      } else {
         super.elementText(builder, element, textNode)
      }
   }
}