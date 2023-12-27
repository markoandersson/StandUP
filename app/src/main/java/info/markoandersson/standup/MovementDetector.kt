package info.markoandersson.standup

import android.util.Log
import java.time.LocalDate
import kotlin.math.abs

/**
 * Detects movement by inspecting accelerometer's Z-value. First value is calibrated in order to detect
 * when the phone is laying still in the table. After that the movement can be detected.
 *
 */
class MovementDetector(
    val onMovingUp : () -> Unit,
    val onMovingDown: () -> Unit
) {

    private val calibration = Calibration(onCalibrated = {
        Log.d("StandUp", "Calibrated to $it")
    })

    private val movementThreshold = Threshold(0.05f, 0.20f)

    fun onSensorValue(z: Float) {

        calibration.onMeasurement(z)

        if (!calibration.isCalibrated()) return

        val average = calibration.average()

        val dz = z - average

        if (movementThreshold.isInThreshold(dz)) {

            if (dz > 0) {
                onMovingUp()
            }
            else {
                onMovingDown()
            }
        }
    }

}
