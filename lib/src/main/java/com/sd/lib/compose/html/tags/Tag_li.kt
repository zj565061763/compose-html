package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

class Tag_li(
   private val dot: String = "• ",
) : TagBlock() {
   override fun elementText(builder: AnnotatedString.Builder, element: Element, textNode: TextNode) {
      builder.append(dot)
      super.elementText(builder, element, textNode)
   }
}