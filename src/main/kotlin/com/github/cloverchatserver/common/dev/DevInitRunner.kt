package com.github.cloverchatserver.common.dev

import com.github.cloverchatserver.domain.chatroom.business.data.ChatRoomCreation
import com.github.cloverchatserver.domain.chatroom.persistence.ChatRoomType
import com.github.cloverchatserver.domain.chatroom.business.ChatRoomService
import com.github.cloverchatserver.domain.account.business.data.RequestUserCreateForm
import com.github.cloverchatserver.domain.account.persistence.Account
import com.github.cloverchatserver.domain.account.business.AccountService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class DevInitRunner(

    val chatRoomService: ChatRoomService,
    val accountService: AccountService

) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        if (isProd()) {
            return
        }

        val users = createUsers(1, 5)
        for (i in 1..5) {
            val chatRoomCreation = ChatRoomCreation(users[0].id!!, null, "title$i", ChatRoomType.PUBLIC)
            chatRoomService.createChatRoom(chatRoomCreation)
        }
    }

    private fun isProd(): Boolean {
        val user = accountService.findById(1L)

        return user != null
    }

    private fun createUsers(initNum: Int, size: Int): List<Account> {
        val result = ArrayList<Account>()

        val maxNum = initNum + size - 1
        for (i in initNum .. maxNum) {
            val form = RequestUserCreateForm("user${i}@gmail.com", "1234", "user$i")
            val account: Account = accountService.create(form)

            result.add(account)
        }

        return result
    }
}