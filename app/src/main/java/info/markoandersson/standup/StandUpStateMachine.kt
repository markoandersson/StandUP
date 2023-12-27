package info.markoandersson.standup

import android.util.Log
import org.jeasy.states.api.AbstractEvent
import org.jeasy.states.api.Event
import org.jeasy.states.api.EventHandler
import org.jeasy.states.api.FiniteStateMachine
import org.jeasy.states.api.State
import org.jeasy.states.api.Transition
import org.jeasy.states.core.FiniteStateMachineBuilder
import org.jeasy.states.core.TransitionBuilder

internal class UpwardAcceleration : AbstractEvent("UpwardAcceleration")
internal class DownwardAcceleration : AbstractEvent("DownwardAcceleration")

class StandUpStateMachine {

    constructor(startState: State) {
        this.startState = startState
    }

    constructor()

    companion object {
        val unknown = State("unknown")
        val raising = State("raising")
        val lowering = State("lowering")
        val standing = State("standing")
        val sitting = State("sitting")
    }


    private var startState: State = unknown

    private val states = setOf(unknown, standing, raising, sitting, lowering)


    fun create(notify: (State) -> Unit = {}): FiniteStateMachine {

        val stateMachine = FiniteStateMachineBuilder(states, startState)

            .registerTransition(
                TransitionBuilder()
                    .name("raising from unknown")
                    .sourceState(unknown)
                    .eventType(UpwardAcceleration::class.java)
                    .eventHandler(Raising())
                    .targetState(raising)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("lowering from unknown")
                    .sourceState(unknown)
                    .eventType(DownwardAcceleration::class.java)
                    .eventHandler(Lowering())
                    .targetState(lowering)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("raising to standing")
                    .sourceState(raising)
                    .eventType(DownwardAcceleration::class.java)
                    .eventHandler(Standing())
                    .targetState(standing)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("lowering to sitting")
                    .sourceState(lowering)
                    .eventType(UpwardAcceleration::class.java)
                    .eventHandler(Sitting())
                    .targetState(sitting)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("standing to lowering")
                    .sourceState(standing)
                    .eventType(DownwardAcceleration::class.java)
                    .eventHandler(Lowering())
                    .targetState(lowering)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("sitting to raising")
                    .sourceState(sitting)
                    .eventType(UpwardAcceleration::class.java)
                    .eventHandler(Raising())
                    .targetState(raising)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("sitting to lowering")
                    .sourceState(sitting)
                    .eventType(DownwardAcceleration::class.java)
                    .eventHandler(Lowering())
                    .targetState(lowering)
                    .build()
            )
            .registerTransition(
                TransitionBuilder()
                    .name("standing to raising")
                    .sourceState(standing)
                    .eventType(UpwardAcceleration::class.java)
                    .eventHandler(Raising())
                    .targetState(raising)
                    .build()
            )
            .build()

        return StateMachineNotifier(stateMachine, notify)

    }

}

class StateMachineNotifier(private val stateMachine: FiniteStateMachine, val notify: (State) -> Unit) :
    FiniteStateMachine {

    private var lastEventName : String? = null
    private var lastEventTime : Long? = null
    override fun getCurrentState(): State {
        return stateMachine.currentState
    }

    override fun getInitialState(): State {
        return stateMachine.initialState
    }

    override fun getFinalStates(): MutableSet<State> {
        return stateMachine.finalStates
    }

    override fun getStates(): MutableSet<State> {
        return stateMachine.states
    }

    override fun getTransitions(): MutableSet<Transition> {
        return stateMachine.transitions
    }

    override fun getLastEvent(): Event {
        return stateMachine.lastEvent
    }

    override fun getLastTransition(): Transition {
        return stateMachine.lastTransition
    }

    override fun fire(event: Event?): State {

        if (lastEventName != null && lastEventName == event?.name && lastEventTime!! + 500 > event!!.timestamp) {
            // Events too close to each other, ignore
            return stateMachine.currentState
        }

        val newState = stateMachine.fire(event)

        notify(newState)

        lastEventName = event?.name
        lastEventTime = event?.timestamp

        return newState
    }
}

class Sitting : EventHandler<Event> {
    override fun handleEvent(event: Event?) {
        Log.d("StandUp", "Now sitting")
    }

}

class Standing : EventHandler<Event> {
    override fun handleEvent(event: Event?) {
        Log.d("StandUp", "Now standing")
    }

}

class Lowering : EventHandler<Event> {
    override fun handleEvent(event: Event?) {
        Log.d("StandUp", "Lowering...")
    }

}

class Raising : EventHandler<Event> {
    override fun handleEvent(event: Event?) {
        Log.d("StandUp", "Raising...")
    }

}
