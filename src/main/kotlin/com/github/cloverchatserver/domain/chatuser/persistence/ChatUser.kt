package com.github.cloverchatserver.domain.chatuser.persistence

import com.github.cloverchatserver.domain.account.persistence.Account
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoom
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_user")
class ChatUser(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_user_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    val chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: Account,

    @Column(nullable = true)
    var latestNum: Int? = null,

    @Column
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Column(unique = true)
    private val idPair: String = "${chatRoom.id}-${account.id}"
}
