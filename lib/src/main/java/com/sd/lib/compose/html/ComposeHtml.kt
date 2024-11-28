package com.sd.lib.compose.html

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.sd.lib.compose.html.ComposeHtml.Tag
import com.sd.lib.compose.html.tags.Tag_a
import com.sd.lib.compose.html.tags.Tag_b
import com.sd.lib.compose.html.tags.Tag_blockquote
import com.sd.lib.compose.html.tags.Tag_body
import com.sd.lib.compose.html.tags.Tag_br
import com.sd.lib.compose.html.tags.Tag_div
import com.sd.lib.compose.html.tags.Tag_em
import com.sd.lib.compose.html.tags.Tag_font
import com.sd.lib.compose.html.tags.Tag_heading
import com.sd.lib.compose.html.tags.Tag_i
import com.sd.lib.compose.html.tags.Tag_li
import com.sd.lib.compose.html.tags.Tag_ol
import com.sd.lib.compose.html.tags.Tag_p
import com.sd.lib.compose.html.tags.Tag_span
import com.sd.lib.compose.html.tags.Tag_strong
import com.sd.lib.compose.html.tags.Tag_u
import com.sd.lib.compose.html.tags.Tag_ul
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.parser.ParseSettings
import org.jsoup.parser.Parser

class ComposeHtml {
   private val _tagFactories = mutableMapOf<String, (Element) -> Tag?>()

   fun setTagFactory(tagName: String, factory: (element: Element) -> Tag?) {
      synchronized(this@ComposeHtml) {
         _tagFactories[tagName] = factory
      }
   }

   fun parse(html: String): Result {
      val parser = Parser.htmlParser().settings(ParseSettings.preserveCase)
      val body = Jsoup.parse(html, parser).body() ?: return Result.Empty

      val inlineContentHolder = ParseInlineContentHolder()
      val annotatedString = buildAnnotatedString {
         val builder = this
         val tag = checkNotNull(newTag(body, inlineContentHolder))

         val skip = tag.elementStart(builder, body)
         if (skip) return@buildAnnotatedString

         val start = length
         parseElement(this, body, tag, inlineContentHolder)
         val end = length

         tag.elementEnd(
            builder = builder,
            element = body,
            start = start,
            end = end,
         )
      }

      return Result(
         text = annotatedString,
         inlineContent = inlineContentHolder.data.toMap(),
      )
   }

   private fun parseElement(
      builder: AnnotatedString.Builder,
      parent: Element,
      parentTag: Tag,
      inlineContentHolder: InlineContentHolder,
   ) {
      for (node in parent.childNodes()) {
         when (node) {
            is TextNode -> {
               parentTag.elementText(
                  builder = builder,
                  element = parent,
                  textNode = node,
               )
            }

            is Element -> {
               val tag = newTag(node, inlineContentHolder)
               if (tag != null) {
                  val skip = tag.elementStart(builder, node)
                  if (skip) continue

                  val start = builder.length
                  parseElement(builder, node, tag, inlineContentHolder)
                  val end = builder.length

                  tag.elementEnd(
                     builder = builder,
                     element = node,
                     start = start,
                     end = end,
                  )
               }
            }
         }
      }
   }

   private fun newTag(element: Element, inlineContentHolder: InlineContentHolder): Tag? {
      val tagName = element.tagName()
      val tag = synchronized(this@ComposeHtml) {
         _tagFactories[tagName]?.invoke(element)
      } ?: newDefaultTag(tagName)
      return tag?.also {
         it.inlineContentHolder = inlineContentHolder
      }
   }

   data class Result(
      val text: AnnotatedString,
      val inlineContent: Map<String, InlineTextContent>,
   ) {
      companion object {
         val Empty = Result(
            text = AnnotatedString(""),
            inlineContent = emptyMap(),
         )
      }
   }

   abstract class Tag {
      open fun elementStart(
         builder: AnnotatedString.Builder,
         element: Element,
      ): Boolean = false

      open fun elementText(
         builder: AnnotatedString.Builder,
         element: Element,
         textNode: TextNode,
      ) {
         builder.append(textNode.text())
      }

      open fun elementEnd(
         builder: AnnotatedString.Builder,
         element: Element,
         start: Int, end: Int,
      ) = Unit

      internal lateinit var inlineContentHolder: InlineContentHolder

      protected fun addInlineContent(
         id: String,
         placeholderWidth: TextUnit = 1.em,
         placeholderHeight: TextUnit = 1.em,
         placeholderVerticalAlign: PlaceholderVerticalAlign = PlaceholderVerticalAlign.TextBottom,
         content: @Composable (String) -> Unit,
      ) {
         addInlineContent(
            id = id,
            placeholder = Placeholder(
               width = placeholderWidth,
               height = placeholderHeight,
               placeholderVerticalAlign = placeholderVerticalAlign,
            ),
            content = content,
         )
      }

      protected fun addInlineContent(
         id: String,
         placeholder: Placeholder,
         content: @Composable (String) -> Unit,
      ) {
         inlineContentHolder.addInlineTextContent(
            id = id,
            placeholder = placeholder,
            content = content,
         )
      }
   }
}

internal interface InlineContentHolder {
   fun addInlineTextContent(
      id: String,
      placeholder: Placeholder,
      content: @Composable (String) -> Unit,
   )
}

private class ParseInlineContentHolder : InlineContentHolder {
   val data = mutableMapOf<String, InlineTextContent>()

   override fun addInlineTextContent(
      id: String,
      placeholder: Placeholder,
      content: @Composable (String) -> Unit,
   ) {
      data[id] = InlineTextContent(placeholder, content)
   }
}

private fun newDefaultTag(tagName: String): Tag? {
   return when (tagName) {
      "body" -> Tag_body()
      "p" -> Tag_p()
      "div" -> Tag_div()
      "a" -> Tag_a()
      "br" -> Tag_br()
      "ul" -> Tag_ul()
      "li" -> Tag_li()
      "span" -> Tag_span()
      "b" -> Tag_b()
      "strong" -> Tag_strong()
      "em" -> Tag_em()
      "i" -> Tag_i()
      "u" -> Tag_u()
      "font" -> Tag_font()
      "h1" -> Tag_heading.level(1)
      "h2" -> Tag_heading.level(2)
      "h3" -> Tag_heading.level(3)
      "h4" -> Tag_heading.level(4)
      "h5" -> Tag_heading.level(5)
      "h6" -> Tag_heading.level(6)
      "blockquote" -> Tag_blockquote()
      "ol" -> Tag_ol()
      else -> null
   }
}