//package org.qbrp.engine.client.render.screen.chat
//
//import icyllis.modernui.util.DataSetObserver
//import icyllis.modernui.view.View
//import icyllis.modernui.view.ViewGroup
//import icyllis.modernui.widget.BaseAdapter
//import icyllis.modernui.widget.TextView
//import org.qbrp.engine.client.engine.chat.system.MessageProvider
//
//class ChatAdapter(private var messages: List<MessageProvider.VisibleMessage>) : BaseAdapter() {
//
//    private val observers: MutableList<DataSetObserver> = mutableListOf()
//
//    override fun registerDataSetObserver(observer: DataSetObserver) { observers.add(observer) }
//    override fun unregisterDataSetObserver(observer: DataSetObserver) { observers.remove(observer) }
//
//    override fun getCount(): Int = messages.size
//    override fun getItem(position: Int): Any? = messages[position]
//    override fun getItemId(position: Int): Long = position.toLong()
//    override fun hasStableIds(): Boolean = false
//
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val textView = convertView as? TextView ?: TextView(parent.context).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        }
//        textView.text = messages[position].text
//        return textView
//    }
//
//    override fun getItemViewType(position: Int): Int = 0
//    override fun getViewTypeCount(): Int = 1
//    override fun isEmpty(): Boolean = messages.isEmpty()
//
//    fun updateMessages(newMessages: List<MessageProvider.VisibleMessage>) {
//        messages = newMessages
//        observers.forEach { it.onChanged() }
//        notifyDataSetChanged()
//        notifyDataSetInvalidated()
//    }
//
//    override fun areAllItemsEnabled(): Boolean = true
//    override fun isEnabled(position: Int): Boolean = true
//}
