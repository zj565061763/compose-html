package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import org.jsoup.nodes.Element

internal class Tag_li : TagBlock() {
   override fun elementStart(builder: AnnotatedString.Builder, element: Element): Boolean {
      super.elementStart(builder, element)
      builder.append("•")
      return false
   }
}