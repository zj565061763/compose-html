package com.sd.lib.compose.html

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import org.jsoup.nodes.Node

internal fun Node.styleTextAlign(): TextAlign? {
   val result = sTextAlignRegex.find(style()) ?: return null
   return when (result.groups[1]?.value) {
      "left" -> TextAlign.Left
      "right" -> TextAlign.Right
      "center" -> TextAlign.Center
      "justify" -> TextAlign.Justify
      "start" -> TextAlign.Start
      "end" -> TextAlign.End
      else -> null
   }
}

internal fun Node.styleColor(): Color? {
   val result = sColorRegex.find(style()) ?: return null
   return result.groups[1]?.value.toComposeColor()
}

internal fun Node.styleBackgroundColor(): Color? {
   val result = sBackgroundColorRegex.find(style()) ?: return null
   return result.groups[1]?.value.toComposeColor()
}

internal fun Node.styleTextDecoration(): TextDecoration? {
   val result = sTextDecorationRegex.find(style()) ?: return null
   return when (result.groups[1]?.value) {
      "underline" -> TextDecoration.Underline
      "line-through" -> TextDecoration.LineThrough
      else -> null
   }
}

private fun Node.style(): String = attr("style") ?: ""

private val sTextAlignRegex = getStyleRegex("text-align").toRegex()
private val sColorRegex = getStyleRegex("color").toRegex()
private val sBackgroundColorRegex = getStyleRegex("background(?:-color)?").toRegex()
private val sTextDecorationRegex = getStyleRegex("text-decoration").toRegex()

private fun getStyleRegex(style: String): String {
   return "(?:\\s+|\\A)${style}\\s*:\\s*(\\S*)\\b"
}

private fun String?.toComposeColor(): Color? {
   val color = this
   if (color.isNullOrBlank()) return null

   if (Character.isLetter(color[0])) {
      return try {
         Color(android.graphics.Color.parseColor(color))
      } catch (e: IllegalArgumentException) {
         null
      }
   }

   return try {
      Color(Integer.decode(color))
   } catch (e: NumberFormatException) {
      null
   }
}