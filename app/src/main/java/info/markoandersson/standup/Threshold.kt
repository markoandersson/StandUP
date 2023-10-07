package info.markoandersson.standup

import kotlin.math.abs

data class Threshold(val min: Float, val max: Float) {

    fun isInThreshold(dz: Float): Boolean {

        return abs(dz) < max && abs(dz) > min
    }

}
