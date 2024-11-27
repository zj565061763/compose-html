package com.sd.demo.compose.html

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sd.lib.compose.html.Factory
import com.sd.lib.compose.html.rememberComposeHtml
import com.sd.lib.compose.html.tags.Tag_img

@Composable
fun AppTextView(
   modifier: Modifier = Modifier,
   html: String,
) {
   BoxWithConstraints(
      modifier = modifier,
   ) {
      val density = LocalDensity.current
      val lineHeight = 18.sp

      val composeHtml = rememberComposeHtml().apply {
         Factory("img") {
            AppTag_img(
               density = density,
               maxWidth = maxWidth,
               lineHeight = lineHeight,
            )
         }
      }

      val annotated = remember(composeHtml, html) { composeHtml.parse(html) }
      val inlineContent by composeHtml.inlineContentFlow.collectAsStateWithLifecycle()

      Text(
         text = annotated,
         color = Color.Black,
         fontSize = 14.sp,
         lineHeight = lineHeight,
         inlineContent = inlineContent,
      )
   }
}

private class AppTag_img(
   density: Density,
   maxWidth: Dp,
   lineHeight: TextUnit,
) : Tag_img(
   density = density,
   maxWidth = maxWidth,
   lineHeight = lineHeight,
) {
   override fun addImgContent(src: String, alt: String, placeholder: Placeholder) {
      addInlineContent(
         id = src,
         placeholder = placeholder,
      ) {
         Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
               .fillMaxSize()
               .background(Color.Green.copy(0.5f))
         ) {
            Image(
               painter = painterResource(R.drawable.cn),
               contentDescription = alt,
               contentScale = ContentScale.FillWidth,
               modifier = Modifier
                  .background(Color.Green.copy(0.2f))
                  .fillMaxWidth(),
            )
         }
      }
   }
}

@Composable
fun AndroidTextView(
   modifier: Modifier = Modifier,
   html: String,
) {
   val spanned = remember(html) {
      Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
   }
   AndroidView(
      modifier = modifier,
      factory = { context ->
         TextView(context).apply {
            this.textSize = 14f
            this.setTextColor(android.graphics.Color.BLACK)
         }
      },
      update = { textView ->
         textView.text = spanned
      }
   )
}
