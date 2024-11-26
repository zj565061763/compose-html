package com.sd.lib.compose.html.tags

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.em
import org.jsoup.nodes.Element

internal class Tag_heading(
   private val style: SpanStyle,
) : TagBlock() {
   override fun elementEnd(builder: AnnotatedString.Builder, element: Element, start: Int, end: Int) {
      super.elementEnd(builder, element, start, end)
      builder.addStyle(
         style = style,
         start = start,
         end = end,
      )
   }

   companion object {
      fun level(level: Int): Tag_heading {
         val fontSize = when (level) {
            1 -> 1.5f
            2 -> 1.4f
            3 -> 1.3f
            4 -> 1.2f
            5 -> 1.1f
            else -> 1.0f
         }
         return Tag_heading(style = SpanStyle(fontSize = fontSize.em))
      }
   }
}