package com.kazeem.paint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import com.kazeem.paint.managers.PaintDrawerManager
import com.kazeem.paint.models.Tools
import com.kazeem.paint.ui.theme.ButtonColor
import com.kazeem.paint.ui.theme.PaintTheme
import com.kazeem.paint.ui.theme.SelectedButtonColor
import com.kazeem.paint.viewModel.MainActivityViewModel
import com.kazeem.paint.viewModel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedStateHandle = SavedStateHandle()
        val viewModelFactory = ViewModelFactory(savedStateHandle)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        setContent {
            PaintTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    PaintView(viewModel)
                }
            }
        }
    }
}

@Composable
private fun PaintView(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = MaterialTheme.shapes.large,
            backgroundColor = ButtonColor
        ) {
            var expandColorPaletteMenu by rememberSaveable {
                mutableStateOf(false)
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .background(color = ButtonColor, shape = MaterialTheme.shapes.large),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items(viewModel.drawingToolList.size) { index ->
                    val toolData = viewModel.drawingToolList[index]
                    Button(
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (viewModel.selectedDrawingTool.value == toolData.toolType) SelectedButtonColor else ButtonColor),
                        onClick = {
                            if (toolData.toolType == Tools.ColorPalette) {
                                expandColorPaletteMenu = true
                            } else {
                                viewModel.selectedDrawingTool.value = toolData.toolType
                            }
                        },
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.defaultMinSize(1.dp, 1.dp),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = toolData.icon),
                            contentDescription = null,
                            tint = if(toolData.toolType == Tools.ColorPalette) viewModel.toolColor.value else Color.Black
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.BottomEnd)
                    .padding(top = 50.dp)
            ) {
                DropdownMenu(
                    expanded = expandColorPaletteMenu,
                    onDismissRequest = { expandColorPaletteMenu = false },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    val colorList = arrayOf(Color.Red, Color.Green, Color.Blue, Color.Black)
                    DropdownMenuItem(
                        onClick = { expandColorPaletteMenu = false },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Row() {
                            colorList.forEach {
                                Button(
                                    shape = MaterialTheme.shapes.small,
                                    colors = ButtonDefaults.buttonColors(backgroundColor = it),
                                    onClick = {
                                        expandColorPaletteMenu = false
                                        viewModel.setToolColor(it)
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier
                                        .size(35.dp)
                                        .padding(3.dp),
                                    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                                ) {

                                }
                            }
                        }
                    }
                }
            }

        }
        val context = LocalContext.current
        AndroidView(factory = { PaintDrawerManager(context)}, modifier = Modifier.padding(top = 10.dp)) {
            it.changeColor(viewModel.toolColor.value.toArgb())
            it.changeTool(viewModel.selectedDrawingTool.value)
        }
    }
}
