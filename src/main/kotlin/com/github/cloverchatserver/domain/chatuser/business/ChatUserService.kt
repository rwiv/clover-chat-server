package com.github.cloverchatserver.domain.chatuser.business

import com.github.cloverchatserver.common.error.exception.HttpException
import com.github.cloverchatserver.domain.chatuser.persistence.ChatUser
import com.github.cloverchatserver.domain.chatuser.persistence.ChatUserRepository
import com.github.cloverchatserver.common.error.exception.NotFoundException
import com.github.cloverchatserver.domain.account.persistence.Account
import com.github.cloverchatserver.domain.account.persistence.AccountRepository
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoomRepository
import com.github.cloverchatserver.domain.chatuser.business.data.ChatUserCreation
import com.github.cloverchatserver.domain.friend.persistence.Friend
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class ChatUserService(
    private val chatUserRepository: ChatUserRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val accountRepository: AccountRepository,
) {

    fun findAll(): MutableList<ChatUser> {
        return chatUserRepository.findAll()
    }

    fun findByAccount(account: Account): List<ChatUser> {
        return chatUserRepository.findByAccount(account)
    }

    fun findByChatRoomId(chatRoomId: Long): List<ChatUser> {
        val chatRoom = chatRoomRepository.findById(chatRoomId).getOrNull()
            ?: throw NotFoundException("not found chatroom")

        return chatUserRepository.findByChatRoom(chatRoom)
    }

    @Transactional
    fun create(creation: ChatUserCreation): ChatUser {
        val chatRoom = chatRoomRepository.findById(creation.chatRoomId).getOrNull()
            ?: throw NotFoundException("not found chatroom")

        if (chatRoom.password !== creation.chatRoomPassword) {
            throw HttpException(403, "invalid password")
        }

        val account = accountRepository.findById(creation.accountId).getOrNull()
            ?: throw NotFoundException("not found account")

        val chatUsers = chatUserRepository.findByChatRoomAndAccount(chatRoom, account)
        if (chatUsers.isNotEmpty()) {
            throw DuplicatedChatUserException("ChatUser is already exist")
        }

        val chatUser = chatUserRepository.save(ChatUser(null, chatRoom, account))
        chatRoom.chatUsers.add(chatUser)
        chatRoom.chatUserCnt += 1

        return chatUser
    }

    @Transactional
    fun createByFriend(chatRoomId: Long, password: String?, friend: Friend): ChatUser {
        return create(ChatUserCreation(chatRoomId, password, friend.to.id!!))
    }

    @Transactional
    fun deleteByAccountId(chatRoomId: Long, accountId: Long): ChatUser {
        val chatRoom = chatRoomRepository.findById(chatRoomId).getOrNull()
            ?: throw NotFoundException("not found chatRoom")
        val account = accountRepository.findById(accountId).getOrNull()
            ?: throw NotFoundException("not found account")

        val chatUsers = chatUserRepository.findByChatRoomAndAccount(chatRoom, account)
        if (chatUsers.size > 1) {
            throw HttpException(400, "duplicated chatUsers")
        }
        if (chatUsers.isEmpty()) {
            throw NotFoundException("not found chatUser")
        }

        chatUserRepository.delete(chatUsers[0])
        chatRoom.chatUserCnt -= 1

        return chatUsers[0]
    }

    @Transactional
    fun updateLatestNum(chatUser: ChatUser, num: Int): ChatUser {
        if (chatUser.latestNum == num) {
            return chatUser
        }
        chatUser.latestNum = num
        return chatUserRepository.save(chatUser)
    }
}
