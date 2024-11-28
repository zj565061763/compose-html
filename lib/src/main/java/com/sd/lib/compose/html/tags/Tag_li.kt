package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import org.jsoup.nodes.Element

class Tag_li(
   private val dot: String = "•",
   private val indent: String = " ",
) : TagBlock() {
   override fun elementStart(builder: AnnotatedString.Builder, element: Element): Boolean {
      return super.elementStart(builder, element).also {
         builder.append(dot)
         builder.append(indent)
      }
   }
}