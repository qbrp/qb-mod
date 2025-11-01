//import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
//import org.qbrp.main.engine.physics.dynamics.dataformats.Vec3Adapter
//import org.qbrp.main.core.util.PlayerId
//import org.qbrp.main.engine.world.location.Position
//import kotlin.math.cos
//import kotlin.math.sin
//
//fun Position.toCellPos() = CellPos(x.toInt(), y.toInt(), z.toInt())
//
//data class CellPos(val x: Int, val y: Int, val z: Int) {
//    fun toPosDouble(): Position = Vec3Adapter(x.toDouble(), y.toDouble(), z.toDouble())
//}
//
//data class Cell(val pos: CellPos, val transmission: Double, val reflection: Double) {
//    companion object {
//        fun air(pos: CellPos) = Cell(pos, 0.98, 0.0)
//        fun solid(pos: CellPos) = Cell(pos, 0.2, 0.7)
//    }
//}
//
//data class Receiver(val pos: CellPos)
//
//data class Scene(val cells: List<Cell>, val players: List<Receiver>) {
//    init { require(!hasDuplicatesBy(cells) { it.pos }) }
//    fun atPos(pos: CellPos): Cell {
//        return cells.find { it.pos == pos } ?: Cell.air(pos)
//    }
//    fun nearestReceivers(pos: CellPos, distance: Double = 64.0): List<Receiver> {
//        return players.filter { it.pos.toPosDouble().squaredDistanceTo(pos.toPosDouble()) < distance * distance}
//    }
//}
//
//data class Source(val content: String, val volume: Double)
//
//data class Receive(val source: Source)
//
//fun <T, K> hasDuplicatesBy(list: List<T>, keySelector: (T) -> K): Boolean {
//    val seen = HashSet<K>()
//    for (e in list) {
//        val key = keySelector(e)
//        if (!seen.add(key)) return true
//    }
//    return false
//}
//
//fun prepareScene(): Scene {
//    val cells = mutableListOf<Cell>()
//    for (x in 0..20) {
//        for (z in 0..20) {
//            cells += Cell.solid(CellPos(x, 0, z))
//        }
//    } //платформа
//
//    for (y in 0..9) {
//        for (z in 0..20) {
//            cells += Cell.solid(CellPos(10, y, z))
//        }
//    } //глушащая стена
//
//    for (x in 0..20) {
//        for (z in 0..20) {
//            cells += Cell.solid(CellPos(x, 10, z),)
//        }
//    } //потолок
//    return Scene(cells, listOf())
//}
//
//fun main() {
//
//}
//
//fun raycast(source: Source, pos: CellPos, scene: Scene, samples: Int): List<Receive> {
//
//    //Рейкаст лучей в разные стороны
//    val raysCount = samples
//    val elevationsCount = samples / 3
//    val TAU = 2 * Math.PI
//    for (rayIndex in 0 until raysCount) {
//        val azimuth = TAU * rayIndex / raysCount
//        for (elevationIndex in 0 until elevationsCount) {
//            val elevation = Math.PI * (elevationIndex.toDouble() / (elevationsCount - 1) - 0.5) // [-π/2, π/2]
//
//            val vector = radToVector(azimuth, elevation).normalize()
//            val startPos = pos.toPosDouble()
//            val step = 1.0
//
//            var volume = source.volume
//            var curPos = startPos
//            while (volume > 0f) {
//                curPos = curPos.add(vector, step)
//                volume *= scene.atPos(curPos.toCellPos()).transmission
//
//                //Рейкастим до ближайших игроков
//                val nearest = scene.nearestReceivers(curPos.toCellPos())
//                val receives = mutableListOf<Receive>()
//                for (receiver in nearest) {
//                    var traceVolume = volume
//                    var curTracePos = curPos
//                    val receiverPos = receiver.pos.toPosDouble()
//                    val toReceiverVector = receiverPos.sub(curTracePos).normalize()
//                    while (curTracePos.squaredDistanceTo(receiverPos) < 1.5f) {
//                        curTracePos = receiverPos.add(toReceiverVector)
//                        traceVolume *= scene.atPos(curPos.toCellPos()).transmission
//                    }
//                    if (traceVolume > 0f) receives.add(Receive(source))
//                }
//            }
//        }
//    }
//}
//
//fun radToVector(azimuth: Double, elevation: Double): Vec3Adapter {
//    val cosTheta = cos(elevation)
//    return Vec3Adapter(
//        cos(azimuth) * cosTheta,
//        sin(elevation),
//        sin(azimuth) * cosTheta
//    )
//}