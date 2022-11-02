package com.example.cloverchatserver.chat.service

import com.example.cloverchatserver.board.service.ChatRoomService
import com.example.cloverchatserver.chat.controller.RequestChatMessage
import com.example.cloverchatserver.chat.repository.ChatMessage
import com.example.cloverchatserver.chat.repository.ChatMessageRepository
import com.example.cloverchatserver.user.service.UserService
import org.springframework.stereotype.Service

@Service
class ChatMessageServiceImpl(

    val chatMessageRepository: ChatMessageRepository,
    val chatRoomService: ChatRoomService,
    val userService: UserService

) : ChatMessageService {

    override fun getChatMessagesBy(chatRoomId: Long): List<ChatMessage> {
        val chatRoom = chatRoomService.getChatRoomBy(chatRoomId)

        return chatMessageRepository.findByChatRoom(chatRoom)
    }

    override fun createChatMessage(requestChatMessage: RequestChatMessage): ChatMessage {
        val chatRoom = chatRoomService.getChatRoomBy(requestChatMessage.chatRoomId)
        val createUser = userService.getUserBy(requestChatMessage.createUserId)

        return chatMessageRepository.save(requestChatMessage.toChatMessage(chatRoom, createUser))
    }
}