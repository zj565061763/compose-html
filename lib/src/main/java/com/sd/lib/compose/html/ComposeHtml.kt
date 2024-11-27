package com.sd.lib.compose.html

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.jsoup.parser.ParseSettings
import org.jsoup.parser.Parser

@Composable
fun rememberComposeHtml(
   enableCache: Boolean = true,
   tagFactory: ((element: Element) -> Tag?)? = null,
): ComposeHtml {
   val tagFactoryUpdated by rememberUpdatedState(tagFactory)
   return remember(enableCache) {
      ComposeHtml(enableCache = enableCache) { element ->
         tagFactoryUpdated?.invoke(element)
      }
   }
}

class ComposeHtml(
   private val enableCache: Boolean = true,
   private val tagFactory: ((element: Element) -> Tag?)? = null,
) {
   private val _parser = Parser.htmlParser().settings(ParseSettings.preserveCase)
   private val _inlineContentFlow = MutableStateFlow<Map<String, InlineTextContent>>(emptyMap())

   private var _cachedHtml = ""
   private var _cachedAnnotatedString: AnnotatedString? = null

   val inlineContentFlow: StateFlow<Map<String, InlineTextContent>>
      get() = _inlineContentFlow.asStateFlow()

   fun parse(html: String): AnnotatedString {
      if (enableCache) {
         synchronized(this@ComposeHtml) {
            _cachedAnnotatedString?.also { cache ->
               if (_cachedHtml == html) return cache
            }
         }
      }

      val body = Jsoup.parse(html, _parser).body() ?: return AnnotatedString("")
      return buildAnnotatedString {
         val builder = this
         val tag = checkNotNull(newTag(body))
         tag.elementStart(
            builder = builder,
            element = body,
         )

         val start = length
         parseElement(this, body, tag)
         val end = length

         tag.elementEnd(
            builder = builder,
            element = body,
            start = start,
            end = end,
         )
      }.also {
         if (enableCache) {
            synchronized(this@ComposeHtml) {
               _cachedHtml = html
               _cachedAnnotatedString = it
            }
         }
      }
   }

   private fun parseElement(builder: AnnotatedString.Builder, parent: Element, parentTag: Tag) {
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
               newTag(node)?.also { tag ->
                  tag.elementStart(
                     builder = builder,
                     element = node,
                  )

                  val start = builder.length
                  parseElement(builder, node, tag)
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

   private fun newTag(element: Element): Tag? {
      return (tagFactory?.invoke(element) ?: DefaultTagFactory(element))
         ?.also { tag ->
            tag.inlineContentHolder = _inlineTextContentHolder
         }
   }

   private val _inlineTextContentHolder = object : InlineContentHolder {
      override fun addInlineTextContent(
         id: String,
         placeholder: Placeholder,
         content: @Composable (String) -> Unit,
      ) {
         _inlineContentFlow.update {
            it + (id to InlineTextContent(placeholder, content))
         }
      }
   }

   abstract class Tag {
      open fun elementStart(
         builder: AnnotatedString.Builder,
         element: Element,
      ) = Unit

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

private val DefaultTagFactory: (Element) -> Tag? = { element: Element ->
   when (element.tagName()) {
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