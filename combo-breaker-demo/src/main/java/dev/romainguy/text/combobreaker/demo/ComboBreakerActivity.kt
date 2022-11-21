/*
 * Copyright (C) 2022 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.romainguy.text.combobreaker.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import dev.romainguy.text.combobreaker.FlowType
import dev.romainguy.text.combobreaker.TextFlow
import dev.romainguy.text.combobreaker.TextFlowHyphenation
import dev.romainguy.text.combobreaker.TextFlowJustification
import dev.romainguy.text.combobreaker.demo.ui.theme.ComboBreakerTheme
import dev.romainguy.text.combobreaker.toContour

//region Sample text
const val SampleText = """The English language does not have definitive hyphenation rules, though various style guides provide detailed usage recommendations and have a significant amount of overlap in what they advise. Hyphens are mostly used to break single words into parts or to join ordinarily separate words into single words. Spaces are not placed between a hyphen and either of the elements it connects except when using a suspended or "hanging" hyphen that stands in for a repeated word (e.g., nineteenth- and twentieth-century writers). Style conventions that apply to hyphens (and dashes) have evolved to support ease of reading in complex constructions; editors often accept deviations if they aid rather than hinder easy comprehension.

The use of the hyphen in English compound nouns and verbs has, in general, been steadily declining. Compounds that might once have been hyphenated are increasingly left with spaces or are combined into one word. Reflecting this changing usage, in 2007, the sixth edition of the Shorter Oxford English Dictionary removed the hyphens from 16,000 entries, such as fig-leaf (now fig leaf), pot-belly (now pot belly), and pigeon-hole (now pigeonhole). The increasing prevalence of computer technology and the advent of the Internet have given rise to a subset of common nouns that might have been hyphenated in the past (e.g., toolbar, hyperlink, and pastebin).

Despite decreased use, hyphenation remains the norm in certain compound-modifier constructions and, among some authors, with certain prefixes (see below). Hyphenation is also routinely used as part of syllabification in justified texts to avoid unsightly spacing (especially in columns with narrow line lengths, as when used with newspapers).

When flowing text, it is sometimes preferable to break a word into two so that it continues on another line rather than moving the entire word to the next line. The word may be divided at the nearest break point between syllables (syllabification) and a hyphen inserted to indicate that the letters form a word fragment, rather than a full word. This allows more efficient use of paper, allows flush appearance of right-side margins (justification) without oddly large word spaces, and decreases the problem of rivers. This kind of hyphenation is most useful when the width of the column (called the "line length" in typography) is very narrow."""
//endregion

class ComboBreakerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialDemoSetup {
                TextFlowDemo()
            }
        }
    }

    @Composable
    private fun TextFlowDemo() {
        val microphone = remember {
            BitmapFactory.decodeResource(resources, R.drawable.microphone)
            .let {
                Bitmap.createScaledBitmap(it, it.width / 2, it.height / 2, true)
            }
        }
        val microphoneShape by remember {
            derivedStateOf { microphone.toContour(alphaThreshold = 0.5f).asComposePath() }
        }

        val badge = remember { BitmapFactory.decodeResource(resources, R.drawable.badge) }
        val badgeShape by remember {
            derivedStateOf { badge.toContour().asComposePath() }
        }

        val letterT = remember { BitmapFactory.decodeResource(resources, R.drawable.letter_t) }
        val landscape = remember { BitmapFactory.decodeResource(resources, R.drawable.landscape) }

        var columns by remember { mutableStateOf(2) }
        var useRectangleShapes by remember { mutableStateOf(false) }
        var isJustified by remember { mutableStateOf(true) }
        var isHyphenated by remember { mutableStateOf(true) }
        var isDebugOverlayEnabled by remember { mutableStateOf(true) }

        val justification by remember {
            derivedStateOf {
                if (isJustified) {
                    TextFlowJustification.Auto
                } else {
                    TextFlowJustification.None
                }
            }
        }
        val hyphenation by remember {
            derivedStateOf {
                if (isHyphenated) {
                    TextFlowHyphenation.Auto
                } else {
                    TextFlowHyphenation.None
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            TextFlow(
                SampleText,
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
                justification = justification,
                hyphenation = hyphenation,
                columns = columns,
                debugOverlay = isDebugOverlayEnabled
            ) {
                Image(
                    bitmap = (if (useRectangleShapes) letterT else microphone).asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .offset {
                            if (useRectangleShapes)
                                IntOffset(0, 0)
                            else
                                Offset(-microphone.width / 4.5f, 0.0f).round()
                        }
                        .flowShape(
                            FlowType.OutsideEnd,
                            if (useRectangleShapes) 0.dp else 8.dp,
                            if (useRectangleShapes) null else microphoneShape
                        )
                )

                Image(
                    bitmap = (if (useRectangleShapes) landscape else badge).asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .flowShape(
                            FlowType.Outside,
                            if (useRectangleShapes) 6.dp else 10.dp,
                            if (useRectangleShapes) null else badgeShape
                        )
                )
            }

            DemoControls(
                columns, { columns = it },
                useRectangleShapes, { useRectangleShapes = it },
                isJustified, { isJustified = it},
                isHyphenated, { isHyphenated = it},
                isDebugOverlayEnabled, { isDebugOverlayEnabled = it},
            )
        }
    }

    @Composable
    private fun DemoControls(
        columns: Int,
        onColumnsChanged: (Int) -> Unit,
        useRectangleShapes: Boolean,
        onRectangleShapesChanged: (Boolean) -> Unit,
        justify: Boolean,
        onJustifyChanged: (Boolean) -> Unit,
        hyphenation: Boolean,
        onHyphenationChanged: (Boolean) -> Unit,
        debugOverlay: Boolean,
        onDebugOverlayChanged: (Boolean) -> Unit,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Columns")
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                modifier = Modifier.weight(0.1f),
                value = columns.toFloat(),
                onValueChange = { onColumnsChanged(it.toInt()) },
                valueRange = 1.0f..4.0f
            )
            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(checked = useRectangleShapes, onCheckedChange = onRectangleShapesChanged)
            Text(text = "Rectangles")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = justify, onCheckedChange = onJustifyChanged)
            Text(text = "Justify")

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(checked = hyphenation, onCheckedChange = onHyphenationChanged)
            Text(text = "Hyphenate")

            Spacer(modifier = Modifier.width(8.dp))

            Checkbox(checked = debugOverlay, onCheckedChange = onDebugOverlayChanged)
            Text(text = "Debug")
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun MaterialDemoSetup(content: @Composable () -> Unit) {
        ComboBreakerTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Combo Breaker Demo") },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
            ) { padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = padding),
                    color = MaterialTheme.colorScheme.surface,
                    content = content
                )
            }
        }
    }
}
