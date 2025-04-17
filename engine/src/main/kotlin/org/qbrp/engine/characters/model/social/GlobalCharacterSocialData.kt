package org.qbrp.engine.characters.model.social

import org.koin.core.component.KoinComponent

data class GlobalCharacterSocialData(
    val socialKey: SocialKey,
    var readDescriptionHash: Int = 0,
    var readLookHash: Int = 0
)