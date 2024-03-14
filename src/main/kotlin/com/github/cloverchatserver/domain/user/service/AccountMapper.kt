package com.github.cloverchatserver.domain.user.service

import com.github.cloverchatserver.domain.user.repository.Account
import org.springframework.stereotype.Component

@Component
class AccountMapper {

    fun toDto(account: Account) = AccountDto(
        id = account.id!!,
        email = account.email,
        password = account.password,
        nickname = account.nickname,
        role = account.role,
    )
}