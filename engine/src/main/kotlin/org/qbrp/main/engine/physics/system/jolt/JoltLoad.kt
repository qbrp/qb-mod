package org.qbrp.main.engine.physics.system.jolt

import com.github.stephengold.joltjni.BroadPhaseLayerInterfaceTable
import com.github.stephengold.joltjni.JobSystem
import com.github.stephengold.joltjni.JobSystemThreadPool
import com.github.stephengold.joltjni.Jolt
import com.github.stephengold.joltjni.JoltPhysicsObject
import com.github.stephengold.joltjni.ObjectLayerPairFilterTable
import com.github.stephengold.joltjni.ObjectVsBroadPhaseLayerFilterTable
import com.github.stephengold.joltjni.PhysicsSystem
import com.github.stephengold.joltjni.TempAllocator
import com.github.stephengold.joltjni.TempAllocatorMalloc
import electrostatic4j.snaploader.LibraryInfo
import electrostatic4j.snaploader.LoadingCriterion
import electrostatic4j.snaploader.NativeBinaryLoader;
import electrostatic4j.snaploader.filesystem.DirectoryPath
import electrostatic4j.snaploader.platform.NativeDynamicLibrary
import electrostatic4j.snaploader.platform.util.PlatformPredicate
import org.qbrp.main.core.Core
import org.qbrp.main.core.fabricmc.server.LogicThread
import org.qbrp.main.engine.physics.system.BUILTIN_GRAVITY
import org.qbrp.main.engine.physics.system.BuiltinPhysicsConfiguration
import org.qbrp.main.engine.physics.system.CollisionLayers
import org.qbrp.main.engine.physics.system.PhysicSystemConfiguration

fun loadJoltNativeLibrary() {
    // Костыль на официальном уровне. В среде разработки игра хоть и запускается в разных директориях, другие библиотеки используют одну общую
    // От чего невозможно одновременно запустить и сервер, и клиент
    // Здесь просто ставятся разные директории для клиента и сервера через enum-ы, ибо других способов указать путь нет
    val dir = if (Core.isClient) DirectoryPath.USER_HOME else DirectoryPath.USER_DIR

    val info = LibraryInfo(null, "joltjni", dir)
    val loader = NativeBinaryLoader(info)
    val libraries = arrayOf(
        NativeDynamicLibrary("linux/aarch64/com/github/stephengold", PlatformPredicate.LINUX_ARM_64),
        NativeDynamicLibrary("linux/armhf/com/github/stephengold", PlatformPredicate.LINUX_ARM_32),
        NativeDynamicLibrary("linux/x86-64/com/github/stephengold", PlatformPredicate.LINUX_X86_64),
        NativeDynamicLibrary("osx/aarch64/com/github/stephengold", PlatformPredicate.MACOS_ARM_64),
        NativeDynamicLibrary("osx/x86-64/com/github/stephengold", PlatformPredicate.MACOS_X86_64),
        NativeDynamicLibrary("windows/x86-64/com/github/stephengold", PlatformPredicate.WIN_X86_64)
    )

    loader.registerNativeLibraries(libraries).initPlatformLibrary()
    loader.loadLibrary(LoadingCriterion.CLEAN_EXTRACTION)
}

fun buildJoltJobSystem(): JobSystem {
    val numWorkerThreads = Runtime.getRuntime().availableProcessors();
    return JobSystemThreadPool(Jolt.cMaxPhysicsJobs, Jolt.cMaxPhysicsBarriers, numWorkerThreads);
}

fun buildJoltPhysicsSystem(config: PhysicSystemConfiguration): PhysicsSystem {
    JoltPhysicsObject.startCleaner()
    Jolt.registerDefaultAllocator()
    Jolt.installDefaultAssertCallback();
    Jolt.installDefaultTraceCallback();

    val success = Jolt.newFactory()
    require(success)
    Jolt.registerTypes()

    val numObjLayers = 3;
    val numBpLayers = 3;
    val BP_DYNAMIC = 0
    val BP_STATIC  = 1

    val ovoFilter = ObjectLayerPairFilterTable(numObjLayers).apply {
        enableCollision(CollisionLayers.PLAYER_DYNAMIC, CollisionLayers.PROP_DYNAMIC)
        enableCollision(CollisionLayers.PROP_DYNAMIC, CollisionLayers.PLAYER_DYNAMIC)
        enableCollision(CollisionLayers.PROP_DYNAMIC, CollisionLayers.PROP_DYNAMIC)
        enableCollision(CollisionLayers.PROP_DYNAMIC, CollisionLayers.WORLD_STATIC)
        enableCollision(CollisionLayers.WORLD_STATIC, CollisionLayers.PROP_DYNAMIC)
        disableCollision(CollisionLayers.PLAYER_DYNAMIC, CollisionLayers.PLAYER_DYNAMIC)
    }

    val layerMap = BroadPhaseLayerInterfaceTable(numObjLayers, numBpLayers).apply {
        mapObjectToBroadPhaseLayer(CollisionLayers.PLAYER_DYNAMIC, BP_DYNAMIC)
        mapObjectToBroadPhaseLayer(CollisionLayers.PROP_DYNAMIC, BP_DYNAMIC)
        mapObjectToBroadPhaseLayer(CollisionLayers.WORLD_STATIC, BP_STATIC)
    }

    val ovbFilter = ObjectVsBroadPhaseLayerFilterTable(layerMap, numBpLayers, ovoFilter, numObjLayers);
    return PhysicsSystem().apply {
        init(config.maxBodies,
            config.numBodyMutexes,
            config.maxBodyPairs,
            config.maxContacts,
            layerMap,
            ovbFilter,
            ovoFilter)
        setGravity(BUILTIN_GRAVITY.jolt())
    }
}

fun buildUpJoltSimulation(
    mainThread: LogicThread,
    system: PhysicsSystem = buildJoltPhysicsSystem(BuiltinPhysicsConfiguration),
    jobSystem: JobSystem = buildJoltJobSystem(),
    allocator: TempAllocator = TempAllocatorMalloc(),
): JoltPhysicsSimulation {
    return JoltPhysicsSimulation(
        system,
        allocator,
        jobSystem,
        mainThread,
    )
}