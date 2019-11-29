package com.g0dkar.samplek8sproj.model

enum class VisitorType(val id: Int) {
    HUMAN(1),
    ORC(2),
    ELF(3),
    DWARF(4),
    UNDEAD(5),
    HALFLING(6);

    companion object {
        suspend fun valueOf(id: Int): VisitorType =
            values()[id - 1]
    }
}
