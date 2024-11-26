package com.sd.lib.compose.html

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
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

open class FComposeHtml {
   private val _tags = mutableMapOf<String, () -> Tag>()
   private val _inlineContentFlow = MutableStateFlow<Map<String, InlineTextContent>>(emptyMap())

   val inlineContentFlow: StateFlow<Map<String, InlineTextContent>>
      get() = _inlineContentFlow.asStateFlow()

   fun parse(
      html: String,
      parser: Parser = Parser.htmlParser().settings(ParseSettings.preserveCase),
   ): AnnotatedString {
      val body = Jsoup.parse(html, parser).body() ?: return AnnotatedString("")
      return buildAnnotatedString {
         val builder = this
         val tag = checkNotNull(newTag("body"))
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
      }
   }

   fun addTag(tag: String, factory: () -> Tag) {
      _tags[tag] = factory
   }

   init {
      addTag("body") { Tag_body() }
      addTag("a") { Tag_a() }
      addTag("b") { Tag_b() }
      addTag("blockquote") { Tag_blockquote() }
      addTag("br") { Tag_br() }
      addTag("div") { Tag_div() }
      addTag("em") { Tag_em() }
      addTag("font") { Tag_font() }
      addTag("h1") { Tag_heading.level(1) }
      addTag("h2") { Tag_heading.level(2) }
      addTag("h3") { Tag_heading.level(3) }
      addTag("h4") { Tag_heading.level(4) }
      addTag("h5") { Tag_heading.level(5) }
      addTag("h6") { Tag_heading.level(6) }
      addTag("i") { Tag_i() }
      addTag("li") { Tag_li() }
      addTag("ol") { Tag_ol() }
      addTag("p") { Tag_p() }
      addTag("span") { Tag_span() }
      addTag("strong") { Tag_strong() }
      addTag("u") { Tag_u() }
      addTag("ul") { Tag_ul() }
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
               newTag(node.tagName())?.also { tag ->
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

   private fun newTag(tagName: String): Tag? {
      return _tags[tagName]?.invoke()?.also { tag ->
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