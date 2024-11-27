package com.sd.demo.compose.html

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.demo.compose.html.theme.AppTheme
import com.sd.lib.compose.html.ComposeHtml
import com.sd.lib.compose.html.rememberComposeHtml
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

class Sample_custom : ComponentActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            Content()
         }
      }
   }
}

@Composable
private fun Content(
   modifier: Modifier = Modifier,
) {
   val html = """
        <p>start<user>hello</user>end</p>
    """.trimIndent()

   val composeHtml = rememberComposeHtml {
      when (it.tagName()) {
         "user" -> Tag_user()
         else -> null
      }
   }

   val annotated = remember(composeHtml, html) { composeHtml.parse(html) }
   val inlineContent by composeHtml.inlineContentFlow.collectAsStateWithLifecycle()

   Column(
      modifier = modifier
         .fillMaxSize()
         .padding(10.dp),
   ) {
      Text(
         text = annotated,
         fontSize = 16.sp,
         inlineContent = inlineContent,
      )
   }
}

private class Tag_user : ComposeHtml.Tag() {
   override fun elementText(builder: AnnotatedString.Builder, element: Element, textNode: TextNode) {
      builder.appendInlineContent(id = "user")
      addInlineContent(
         id = "user",
         placeholderWidth = 200.sp,
         placeholderHeight = 24.sp,
      ) {
         Text(
            text = "${textNode.text()} world",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
         )
      }
   }
}