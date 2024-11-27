package com.sd.lib.compose.html

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.sd.lib.compose.html.ComposeHtml.Tag
import org.jsoup.nodes.Element

@Composable
fun rememberComposeHtml(
   onCreate: ((ComposeHtml) -> Unit)? = null,
): ComposeHtml {
   return remember {
      ComposeHtml().also {
         onCreate?.invoke(it)
      }
   }
}

@Composable
fun ComposeHtml.Factory(
   tagName: String,
   factory: (element: Element) -> Tag?,
) {
   val composeHtml = this
   val factoryUpdated by rememberUpdatedState(factory)
   remember(composeHtml, tagName) {
      composeHtml.setTagFactory(tagName) {
         factoryUpdated(it)
      }
      ""
   }
}