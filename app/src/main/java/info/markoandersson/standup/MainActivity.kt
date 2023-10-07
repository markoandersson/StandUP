package info.markoandersson.standup

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.markoandersson.standup.ui.theme.StandUpTheme
import org.jeasy.states.api.State


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("StandUp", "Init")

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager;
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val stateMachine = StandUpStateMachine().create()

        val movementDetector = MovementDetector(
            onMovingUp = { stateMachine.fire(UpwardAcceleration()) },
            onMovingDown = { stateMachine.fire(DownwardAcceleration()) }
        )

        sensorManager.registerListener(
            AcceleratorSensorListener(movementDetector),
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        setContent {
            StandUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CurrentState(stateMachine.currentState)
                }
            }
        }
    }
}


@Composable
fun CurrentState(state: State, modifier: Modifier = Modifier) {

    val textStyle = TextStyle(fontSize = 24.sp)

    var state by remember {
        mutableStateOf(state)
    }

    Column(modifier = modifier.padding(all = 5.dp)) {

        Row {
            Column {
                Text(text = "Current state:", style = textStyle)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column {
                Text(text = state.name, style = textStyle)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StandUpTheme {
        CurrentState(StandUpStateMachine.unknown)
    }
}

