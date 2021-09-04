package nyx.ktorquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nyx.ktorquiz.Status.*
import nyx.ktorquiz.model.Question
import nyx.ktorquiz.ui.component.Centered
import nyx.ktorquiz.ui.theme.KtorquizTheme
import nyx.ktorquiz.ui.theme.Teal200

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorquizTheme {

                when (val result = viewModel.result.collectAsState().value) {

                    is Loading -> Centered {
                        CircularProgressIndicator()
                        Text("Fetching Questions...", Modifier.padding(top = 10.dp))
                    }

                    is Success -> Content(questions = result.data as List<Question>)

                    is Failure -> Centered {
                        Text("An Error occured!", color = Red)
                        Text(result.exception.message ?: "Unknown error!")
                    }
                }
            }
        }
    }

    @Composable
    fun Content(questions: List<Question>) = Centered {

        var currentPage by remember { mutableStateOf(0) }
        var showAnswer by remember { mutableStateOf(false) }

        val (selectedIndex, selectIndex) = remember { mutableStateOf<Int?>(null) }

        LazyColumn(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(DarkGray)
        ) {
            item { Text(questions[currentPage].text, Modifier.padding(10.dp), color = Teal200) }

            itemsIndexed(questions[currentPage].answers) { index, answer ->

                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(if (selectedIndex == index) LightGray else Transparent)
                        .clickable(!showAnswer) {
                            selectIndex(index)
                        }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = selectedIndex == index,
                        onClick = { if (!showAnswer) selectIndex(index) })
                    Text(answer, Modifier.padding(start = 10.dp))
                }
            }
        }

        if (showAnswer) {

            Text(
                questions[currentPage].answers[questions[currentPage].correctAnswer],
                Modifier.padding(10.dp),
                color = if (selectedIndex == questions[currentPage].correctAnswer) Green else Red,
                fontSize = 20.sp
            )

            if (currentPage < questions.size - 1)
                Button(onClick = {
                    showAnswer = false
                    selectIndex(null)
                    currentPage++

                }) {
                    Text("Show next question")
                }

        } else if (selectedIndex != null) {

            Button(
                onClick = { showAnswer = true },
                Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth()
            ) {
                Text("Check Answer")
            }
        }
    }

    @Composable
    @Preview
    fun PreviewContent() {
        Content(
            listOf(
                Question("What is 1+1?", listOf("2", "4", "a"), 0),
                Question("What is pizza?", listOf("Vegetable", "Italian", "Coke"), 1),
                Question("What is 2+2?", listOf("2", "4", "a"), 1)
            )
        )
    }
}