package com.sd.lib.compose.html.tags

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.sd.lib.compose.html.ComposeHtml
import org.jsoup.nodes.Element

abstract class Tag_img(
   private val density: Density,
   private val maxWidth: Dp,
   private val lineHeight: TextUnit,
) : ComposeHtml.Tag() {
   override fun elementStart(builder: AnnotatedString.Builder, element: Element): Boolean {
      val src = element.attr("src")

      val maxWidthPx = density.run { maxWidth.toPx() }
      val lineHeightPx = density.run { lineHeight.toPx() }

      val width = (element.attr("width").toFloatOrNull() ?: lineHeightPx).coerceIn(0f, maxWidthPx)
      if (width <= 0f) {
         builder.appendInlineContent(id = src)
         return false
      }

      if (width > lineHeightPx) {
         val lineSize = (width / lineHeightPx).toInt() + 1
         repeat(lineSize) { builder.appendLine() }
         builder.appendInlineContent(id = src)
         builder.appendLine()
      } else {
         builder.appendInlineContent(id = src)
      }

      val size = density.run { width.toSp() }
      addImgContent(
         src = src,
         alt = element.attr("alt"),
         placeholder = Placeholder(
            width = size,
            height = size,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
         )
      )
      return false
   }

   protected abstract fun addImgContent(
      src: String,
      alt: String,
      placeholder: Placeholder,
   )
}