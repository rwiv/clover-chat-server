package com.github.cloverchatserver.domain.chatmessage.persistence

//@Component
class UnreadCountMemoryRepository {

    private val map: MutableMap<Long, Int> = HashMap()

    fun set(chatMessageId: Long, count: Int) {
        map[chatMessageId] = count
    }

    fun get(chatMessageId: Long): Int? {
        return map[chatMessageId]
    }
}
