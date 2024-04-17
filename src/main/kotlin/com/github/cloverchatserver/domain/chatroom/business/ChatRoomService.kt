package com.github.cloverchatserver.domain.chatroom.business

import com.github.cloverchatserver.common.error.exception.HttpException
import com.github.cloverchatserver.domain.chatroom.business.data.ChatRoomCreation
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoom
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoomRepository
import com.github.cloverchatserver.domain.account.business.data.AccountResponse
import com.github.cloverchatserver.common.error.exception.NotFoundException
import com.github.cloverchatserver.domain.account.persistence.Account
import com.github.cloverchatserver.domain.account.persistence.AccountRepository
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoomType
import com.github.cloverchatserver.domain.chatuser.business.ChatUserService
import com.github.cloverchatserver.domain.chatuser.business.data.ChatUserCreation
import com.github.cloverchatserver.domain.chatuser.persistence.ChatUserRepository
import com.github.cloverchatserver.domain.friend.persistence.Friend
import com.github.cloverchatserver.domain.friend.persistence.FriendRepository
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class ChatRoomService(
    private val accountRepository: AccountRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatUserRepository: ChatUserRepository,
    private val friendRepository: FriendRepository,
    private val chatUserService: ChatUserService,
) {

    fun findById(chatRoomId: Long): ChatRoom? {
        return chatRoomRepository.findById(chatRoomId).getOrNull()
    }

    fun findByPage(page: Int, size: Int): List<ChatRoom> {
        if (page - 1 < 0) {
            throw HttpException(400, "invalid page number")
        }
        return chatRoomRepository.findPublic(PageRequest.of(page - 1, size)).content
    }

    fun findAll(): List<ChatRoom> = chatRoomRepository.findAll()

    fun findByAccount(account: Account): List<ChatRoom> {
        return chatUserRepository.findByAccount(account).map { it.chatRoom }
    }

    @Transactional
    fun create(creation: ChatRoomCreation): ChatRoom {
        val createdBy = accountRepository.findById(creation.createUserId).getOrNull()
            ?: throw NotFoundException("not found account")
        val chatRoom = chatRoomRepository.save(creation.toTbc(createdBy))

        chatUserService.create(ChatUserCreation(chatRoom.id!!, chatRoom.password, creation.createUserId))

        return chatRoom
    }

    @Transactional
    fun createByFriend(friendId: Long, me: AccountResponse): ChatRoom {
        val friend = friendRepository.findById(friendId).getOrNull()?.also {
            if (it.from.id != me.id) {
                throw HttpException(400, "friend id is invalid")
            }
        } ?: throw NotFoundException("not found friend")

        val chatRoom = create(ChatRoomCreation(me.id, null, "", ChatRoomType.PRIVATE))
        chatRoom.title = "private_${chatRoom.id}"
        chatRoomRepository.save(chatRoom)

        chatUserService.create(ChatUserCreation(chatRoom.id!!, chatRoom.password, friend.to.id!!))

        return chatRoom
    }

    @Transactional
    fun delete(chatRoomId: Long, accountResponse: AccountResponse): ChatRoom {
        val chatRoom = findById(chatRoomId)?.also {
            if (accountResponse.id != it.createdBy.id) {
                throw AccessDeniedException("This user is not ChatRoom CreateUser")
            }
        } ?: throw NotFoundException("not found chatroom")

        chatRoomRepository.delete(chatRoom)
        return chatRoom
    }
}
