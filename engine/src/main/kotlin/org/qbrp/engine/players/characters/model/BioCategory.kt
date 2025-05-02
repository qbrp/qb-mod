package org.qbrp.engine.players.characters.model

import org.qbrp.engine.characters.model.Sex

enum class BioCategory(val displayName: String, val sex: Sex = Sex.OTHER) {
    MAN("Мужчина", Sex.MALE),
    WOMAN("Женщина", Sex.FEMALE),
    BOY("Мальчик", Sex.MALE),
    GIRL("Девочка", Sex.FEMALE),
    YOUNG_WOMAN("Девушка", Sex.FEMALE),
    YOUNG_MAN("Парень", Sex.MALE),
    GRANDPA("Дед", Sex.MALE),
    GRANDMA("Бабушка", Sex.FEMALE),
    CREATURE("Существо", Sex.OTHER),
    HUMAN("Человек", Sex.OTHER),
    ANIMAL("Животное", Sex.OTHER),
    SENSIBLE("Разумный", Sex.OTHER),
    FURRY("Разумный", Sex.OTHER),
}