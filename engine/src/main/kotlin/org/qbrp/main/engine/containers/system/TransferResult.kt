package org.qbrp.main.engine.containers.system

sealed class TransferResult {
    object Success: TransferResult()
    object Failure: TransferResult()
}