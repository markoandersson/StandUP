package info.markoandersson.standup

import kotlin.math.abs

/**
 * Class for calibrating measurements. Receives measurement and checks if the measurement is
 * same as the previous measurements with a certain threshold. After class receives ten measurements that
 * do not differentiate too much from each other, then the class considers measurement calibrated.
 *
 *
 * @property onCalibrated
 */
class Calibration(val onCalibrated: (Float) -> (Unit) = {}) {

    private var _average: Float = 0f

    private val size = 10
    private val values = mutableListOf<Float>()
    private val threshold = 0.02f
    private var isCalibrated = false

    fun onMeasurement(z: Float) {

        if (isCalibrated) return

        values.add(z)
        val average = values.average().toFloat()
        values.removeIf { value: Float -> abs(value - threshold) > average  }

        if (values.count() == size) {
            isCalibrated = true
            onCalibrated(average)
            _average = average
        }
    }

    fun isCalibrated() : Boolean {
        return values.count() == size
    }

    fun average() : Float {

        if (!isCalibrated()) {
            return 0f
        }

        return _average
    }

}
