package org.qbrp.main.engine.controls.dsl

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.engine.controls.entity.InteractableTypes
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.interactable.InteractableType
import org.qbrp.main.engine.controls.entity.interactions.Executor
import org.qbrp.main.engine.controls.entity.interactions.Interaction
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext
import org.qbrp.main.engine.controls.entity.interactions.InteractionKind
import org.qbrp.main.engine.controls.model.player.condition.PermissionCondition
import org.qbrp.main.engine.text.components.TextComponent
import kotlin.reflect.KClass

@DslMarker annotation class InteractionDSL

class InteractionDslContext(val permissionService: PermissionService)

@InteractionDSL
context(ctx: InteractionDslContext)
fun <Sub: Interactable, Obj: Interactable> interaction(
    subjectClass: KClass<Sub>,
    objectClass: KClass<Obj>,
    build: InteractionScope<Sub, Obj>.() -> Unit
): InteractionNode<Sub, Obj> {
    return InteractionScope<Sub, Obj>(subjectClass, objectClass, ctx).apply { build() }.buildNode()
}

@InteractionDSL
inline fun <reified Sub: Interactable, reified Obj: Interactable> interaction(noinline build: InteractionScope<Sub, Obj>.() -> Unit): InteractionNode<Sub, Obj> {
    return with(GlobalContext.get().get<InteractionDslContext>()) {
        interaction(Sub::class, Obj::class, build)
    }
}

fun <Sub: Interactable, Obj: Interactable> InteractionNode<Sub, Obj>.finish(): Interaction<Sub, Obj> {
    return InteractionImpl(
        kind ?: throw IllegalStateException("Не указан идентификатор взаимодействия"),
        objectType ?: throw NullPointerException("Не указан тип объекта"),
        subjectType ?: throw NullPointerException("Не указан тип субъекта"),
        priority ?: 5,
        conditions,
        executors,
    )
}

class InteractionImpl<Sub: Interactable, Obj: Interactable>(
    override val kind: InteractionKind,
    override val obj: InteractableType<Obj>,
    override val subject: InteractableType<Sub>,
    override val priority: Int,
    override val conditions: List<Condition<Sub, Obj>>,
    override val executors: List<Executor<Sub, Obj>>,
) : Interaction<Sub, Obj>

data class InteractionNode<Sub: Interactable, Obj: Interactable>(
    val kind: InteractionKind?,
    val subjectType: InteractableType<Sub>?,
    val objectType: InteractableType<Obj>?,
    val conditions: List<Condition<Sub, Obj>>,
    val executors: List<Executor<Sub, Obj>>,
    val priority: Int?,
)

@InteractionDSL
class InteractionScope<Sub: Interactable, Obj: Interactable>(
    private val subjectClass: KClass<Sub>,
    private val objectClass: KClass<Obj>,
    private val context: InteractionDslContext,
) {
    private var subjectType: InteractableType<Sub>? = null
    private var objectType: InteractableType<Obj>? = null
    private val conditions: MutableList<Condition<Sub, Obj>> = mutableListOf()
    private val executors: MutableList<Executor<Sub, Obj>> = mutableListOf()
    private var priority: Int? = null
    private var kind: InteractionKind? = null

    @InteractionDSL
    inner class TypeScope(val scope: InteractionScope<Sub, Obj>) {
        fun subject(type: InteractableType<Sub>) {
            scope.subjectType = type
        }

        fun subject(selection: InteractableTypes.() -> InteractableType<Sub>) {
            scope.subjectType = InteractableTypes.selection()
        }

        fun obj(type: InteractableType<Obj>) {
            scope.objectType = type
        }

        fun obj(selection: InteractableTypes.() -> InteractableType<Obj>) {
            scope.objectType = InteractableTypes.selection()
        }

        fun kind(id: String) {
            scope.kind = InteractionKind(id)
        }
    }

    fun priority(value: Int) {
        priority = value
    }

    fun extends(node: InteractionNode<in Sub, in Obj>) {
        if (subjectType == null && node.subjectType != null) subjectType = node.subjectType as InteractableType<Sub>
        if (objectType == null && node.objectType != null) objectType = node.objectType as InteractableType<Obj>
        if (priority == null && node.priority != null) priority = node.priority
        conditions.addAll(node.conditions)
        executors.addAll( node.executors)
    }

    fun extends(node: Interaction<in Sub, in Obj>) {
        if (subjectType == null) subjectType = node.subject as InteractableType<Sub>
        if (objectType == null) objectType = node.obj as InteractableType<Obj>
        if (priority == null) priority = node.priority
        conditions.addAll(node.conditions)
        executors.addAll( node.executors)
    }

    fun permission(permission: Permission) {
        condition(PermissionCondition(permission, context.permissionService))
    }

    fun condition(condition: Condition<Sub, Obj>) {
        conditions += condition
    }

    fun condition(availableCondition: (Sub, Obj, InteractionContext) -> AvailableResult) {
        conditions += object : Condition<Sub, Obj> {
            override fun isAvailable(sub: Sub, obj: Obj, ctx: InteractionContext): AvailableResult {
                return availableCondition(sub, obj, ctx)
            }
        }
    }

    fun execute(executor: Executor<Sub, Obj>) {
        executors += executor
    }

    fun identity(build: TypeScope.() -> Unit) {
        TypeScope(this).build()
    }

    internal fun buildNode() = InteractionNode(
        kind,
        subjectType,
        objectType,
        conditions,
        executors,
        priority,
    )
}

