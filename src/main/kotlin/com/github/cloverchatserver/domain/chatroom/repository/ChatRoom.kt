package com.github.cloverchatserver.domain.chatroom.repository

import com.github.cloverchatserver.domain.chatroom.controller.domain.ResponseChatRoom
import com.github.cloverchatserver.domain.chatmsg.repository.ChatMessage
import com.github.cloverchatserver.domain.chatuser.repository.ChatUser
import com.github.cloverchatserver.domain.user.repository.Account
import java.lang.RuntimeException
import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "chat_room")
class ChatRoom(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    val id: Long?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val createAccount: Account,

    @Column(length = 20, nullable = true, updatable = false)
    val password: String?,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, updatable = false)
    val createDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ChatRoomType,

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = [ CascadeType.REMOVE ])
    val chatMessages: MutableList<ChatMessage> = ArrayList(),

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = [ CascadeType.REMOVE ])
    val chatUsers: MutableList<ChatUser> = ArrayList()

) {

    fun toResponseChatRoom(): ResponseChatRoom {
        if (id == null) throw RuntimeException()

        return ResponseChatRoom(id!!, createAccount.toResponseUser(), title, createDate, type)
    }
}