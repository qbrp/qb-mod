package org.qbrp.main.engine.containers.v2.system

sealed class TransferResult {
    object Success: TransferResult()
    object Failure: TransferResult()
}