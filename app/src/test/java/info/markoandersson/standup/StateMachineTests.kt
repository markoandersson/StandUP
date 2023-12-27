package info.markoandersson.standup

import info.markoandersson.standup.StandUpStateMachine.Companion.lowering
import info.markoandersson.standup.StandUpStateMachine.Companion.raising
import info.markoandersson.standup.StandUpStateMachine.Companion.sitting
import info.markoandersson.standup.StandUpStateMachine.Companion.standing
import info.markoandersson.standup.StandUpStateMachine.Companion.unknown
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.states.api.State
import org.junit.Test

class StateMachineTests {

    @Test
    fun shouldStartInUnknownState() {
        val stateMachine = StandUpStateMachine().create()

        assertThat(stateMachine.currentState)
            .isEqualTo(unknown)
    }

    @Test
    fun shouldStandUpFromUnknown() {
        val stateMachine = StandUpStateMachine().create()

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(raising)

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(standing)
    }

    @Test
    fun shouldSitDownFromUnknown() {
        val stateMachine = StandUpStateMachine().create()

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(lowering)

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(sitting)
    }

    @Test
    fun shouldStandUpFromSitting() {
        val stateMachine = StandUpStateMachine(sitting).create()

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(raising)

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(standing)

    }

    @Test
    fun shouldSitDownFromStanding() {
        val stateMachine = StandUpStateMachine(standing).create()

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(lowering)

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(sitting)

    }

    @Test
    fun shouldContinueSitting() {
        val stateMachine = StandUpStateMachine(sitting).create()

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(lowering)

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(sitting)

    }

    @Test
    fun shouldContinueStanding() {
        val stateMachine = StandUpStateMachine(standing).create()

        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(raising)

        stateMachine.fire(DownwardAcceleration())

        assertThat(stateMachine.currentState)
            .isEqualTo(standing)

    }

    @Test
    fun shouldIgnoreRepeatedEvents() {

        val stateMachine = StandUpStateMachine(standing).create()

        stateMachine.fire(DownwardAcceleration())
        assertThat(stateMachine.currentState).isEqualTo(lowering)

        stateMachine.fire(UpwardAcceleration())
        assertThat(stateMachine.currentState).isEqualTo(sitting)

        Thread.sleep(250)

        stateMachine.fire(UpwardAcceleration())
        assertThat(stateMachine.currentState)
            .describedAs("Fast repeated events should be ignored")
            .isEqualTo(sitting)
    }

    @Test
    fun shouldNotIgnoreSeparateEvents() {

        val stateMachine = StandUpStateMachine(standing).create()

        stateMachine.fire(DownwardAcceleration())

        stateMachine.fire(UpwardAcceleration())

        Thread.sleep(1000)
        stateMachine.fire(UpwardAcceleration())

        assertThat(stateMachine.currentState)
            .describedAs("Separate events should not be ignored")
            .isEqualTo(raising)
    }

    @Test
    fun shouldNotifyStateChange() {

        var hasBeenNotified = false

        val notify: (state : State) -> Unit = {
            hasBeenNotified = true
        }

        val stateMachine = StandUpStateMachine().create(notify)

        stateMachine.fire(UpwardAcceleration())

        assertThat(hasBeenNotified).isTrue()

    }
}