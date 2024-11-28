package com.sd.demo.compose.html

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.demo.compose.html.theme.AppTheme

class Sample : ComponentActivity() {

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
   Column(
      modifier = modifier
         .fillMaxSize()
         .verticalScroll(rememberScrollState())
         .padding(10.dp),
   ) {
      HorizontalDivider()
      AppTextView(html = htmlContent)
      HorizontalDivider()
      AndroidTextView(html = htmlContent)
      HorizontalDivider()
   }
}

val htmlContent =
   """<body><h1>h1</h1><h2>h2</h2><h3>h3</h3><h4>h4</h4><h5>h5</h5><h6>h6</h6><p style="text-align: start;">start</p><p style="text-align: center;">center</p><p style="text-align: end;">end</p><p style="color: green;">color</p><p style="background-color: red;">background-color</p><p style="text-decoration: line-through;">decoration line through</p><p style="text-decoration: underline;">decoration underline</p><p>b:<b>bbb</b></p><p>strong:<strong>strong</strong></p><p>i:<i>iii</i></p><p>em:<em>em</em></p><p>u:<u>uuu</u></p><p>test a<a href="https://www.baidu.com">click</a>here</p><ul><li>ul li 1</li><li>ul li 2</li></ul><ol><li>ol li 1</li><li>ol li 2</li></ol><blockquote>This is a blockquote.</blockquote><p>Here is a line break:</p>before br<br/>after br<p><span>span1</span></p><p><span>span2</span></p><p><span>span3</span></p><p><span>span4</span></p><p><span>span5</span></p><p>image:<img src=""alt=""/></p><div>div<div>div1</div><div>div2</div><div>div3</div><div>div4</div><div>div5</div></div><p>end</p></body>"""