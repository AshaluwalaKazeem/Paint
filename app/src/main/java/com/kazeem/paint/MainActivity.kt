package com.kazeem.paint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
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
                            viewModel.selectedDrawingTool.value = toolData.toolType
                        },
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.defaultMinSize(1.dp, 1.dp),
                        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = toolData.icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
