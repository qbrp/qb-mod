package engine.interactions

import org.qbrp.main.engine.controls.entity.interactable.AnyInteractableType
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.display.IconTextureId
import org.qbrp.main.engine.controls.entity.display.InteractableViewData
import org.qbrp.main.engine.controls.entity.display.InteractionIcon
import org.qbrp.main.engine.controls.entity.display.InteractableName
import org.qbrp.main.engine.text.literal

class DummyInteractable(val name: String, override val interactableType: AnyInteractableType) : Interactable {
    override val interactableDisplay: InteractableViewData =
        InteractableViewData(
            InteractableName(literal(name)),
            InteractionIcon.Asset(IconTextureId("")),
            0
        )
}