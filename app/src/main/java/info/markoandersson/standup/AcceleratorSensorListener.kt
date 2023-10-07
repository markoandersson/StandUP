package info.markoandersson.standup

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AcceleratorSensorListener(private val movementDetector: MovementDetector) : SensorEventListener {

    private val gravity = SensorManager.GRAVITY_EARTH

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Process accelerometer data here
            val z = event.values[2] - gravity
            movementDetector.onSensorValue(z)
        }
    }
}