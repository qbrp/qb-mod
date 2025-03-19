//package org.qbrp.engine.client.render.screen.chat
//
//import icyllis.modernui.core.Core
//import icyllis.modernui.fragment.Fragment
//import icyllis.modernui.graphics.drawable.ShapeDrawable
//import icyllis.modernui.mc.ScreenCallback
//import icyllis.modernui.util.DataSet
//import icyllis.modernui.view.Gravity
//import icyllis.modernui.view.KeyEvent
//import icyllis.modernui.view.LayoutInflater
//import icyllis.modernui.view.View
//import icyllis.modernui.view.ViewGroup
//import icyllis.modernui.widget.FrameLayout
//import icyllis.modernui.widget.LinearLayout
//import icyllis.modernui.widget.LinearLayout.LayoutParams
//import icyllis.modernui.widget.ListView
//import net.minecraft.util.ActionResult
//import org.qbrp.engine.client.EngineClient
//import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
//import org.qbrp.engine.client.render.Colors
//
//class ChatScreen : Fragment(), ScreenCallback {
//    private lateinit var chatAdapter: ChatAdapter
//    private lateinit var editText: ComponentsInputWidget
//    private val api = EngineClient.getChatModuleAPI()!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: DataSet?
//    ): View? {
//        val rootLayout = FrameLayout(requireContext()).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        val chatContainer = LinearLayout(context).apply {
//            orientation = LinearLayout.VERTICAL
//            gravity = Gravity.CENTER
//            background = createBgDrawable(Colors.DARK_BACKGROUND)
//            layoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.BOTTOM
//            }
//            setPadding(7, 7, 7, 7)
//        }
//
//        val messagesView = ListView(context).apply {
//            layoutParams = LayoutParams(
//                800,
//                (context.resources.displayMetrics.heightPixels * 0.7).toInt(),
//            )
//            isVerticalFadingEdgeEnabled = true
//            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
//        }
//
//        editText = ComponentsInputWidget(context).apply {
//            hint = "Введите сообщение..."
//            setHintTextColor(Colors.black(190))
//            isFocusable = true
//            isFocusableInTouchMode = true
//            isFocusedByDefault = true
//            layoutParams = LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT
//            )
//            setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEY_ENTER) {
//                    when (event.action) {
//                        KeyEvent.ACTION_DOWN -> {
//                            // Перехватываем событие, чтобы предотвратить автоматический перенос строки
//                            true
//                        }
//                        KeyEvent.ACTION_UP -> {
//                            if (event.isShiftPressed()) {
//                                // Если зажат Shift, вручную добавляем перевод строки
//                                text.append("\n")
//                            } else if (text.toString().trim().isNotEmpty()) {
//                                // Если Shift не зажат и поле не пустое, отправляем сообщение
//                                api.sendMessageToServer(
//                                    api.createMessageFromContext(
//                                        api.getTypingContextFromText(text.toString())
//                                    )
//                                )
//                                setText("")
//                                // Если требуется, можно также добавить отложенный вызов requestFocus() здесь,
//                                // но в нашем случае ниже будет запущен метод ensureFocus()
//                            }
//                            true
//                        }
//                        else -> false
//                    }
//                } else if (keyCode == KeyEvent.KEY_ESCAPE && event.action == KeyEvent.ACTION_UP) {
//                    true
//                } else {
//                    false
//                }
//            }
//        }
//
//        chatAdapter = ChatAdapter(api.getMessages())
//        messagesView.adapter = chatAdapter
//
//        chatContainer.addView(messagesView)
//        chatContainer.addView(editText)
//        rootLayout.addView(chatContainer)
//        chatContainer.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
//        messagesView.isFocusable = false
//        messagesView.isFocusableInTouchMode = false
//
//        editText.post() {
//            editText.requestFocus()
//        }
//        return rootLayout
//    }
//
//    init {
//        MessageAddedEvent.EVENT.register { message, storage ->
//            Core.getUiHandler().post {
//                updateMessages()
//            }
//            ActionResult.PASS
//        }
//    }
//
//    fun updateMessages() {
//        chatAdapter.updateMessages(api.getMessages())
//    }
//
//    fun createBgDrawable(color: Int): ShapeDrawable {
//        return ShapeDrawable().apply {
//            shape = ShapeDrawable.RECTANGLE
//            setColor(color)
//            setPadding(5, 5, 5, 5)
//        }
//    }
//
//    override fun hasDefaultBackground(): Boolean = false
//    override fun shouldBlurBackground(): Boolean = false
//}
