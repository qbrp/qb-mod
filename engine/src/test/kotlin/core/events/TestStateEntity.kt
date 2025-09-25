package core.events

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.components.state.ComponentHolder

class TestStateEntity(): StateObject<ComponentManager>(ComponentHolder())