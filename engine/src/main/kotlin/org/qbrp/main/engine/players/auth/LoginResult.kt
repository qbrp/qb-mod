package org.qbrp.main.engine.players.auth

import org.qbrp.main.engine.players.accounts.Account

sealed class LoginResult {
    class Success(val account: Account): LoginResult()
    class AlreadyLinked : LoginResult()
    class NotFound(): LoginResult()
    class Error(val ex: Exception): LoginResult()
}