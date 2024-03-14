package com.github.cloverchatserver.security.authentication

import com.github.cloverchatserver.domain.account.api.domain.ResponseUser
import com.github.cloverchatserver.domain.account.business.AccountService
import com.github.cloverchatserver.error.exception.HttpException
import com.github.cloverchatserver.security.account.AccountDetails
import com.github.cloverchatserver.security.account.AccountDetailsService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AuthenticationTokenProvider(
    val accountService: AccountService,
    val accountDetailsService: AccountDetailsService,
    val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {

    @Transactional
    override fun authenticate(authentication: Authentication): Authentication {
        val req = LoginForm.of(authentication as AuthenticationToken)
        val accountDetails: AccountDetails = accountDetailsService.loadUserByUsername(req.username)

        if (!passwordEncoder.matches(req.password, accountDetails.password)) {
            throw HttpException(401, "Invalid password")
        }

        val account = accountService.getUserBy(accountDetails.account.id)
            ?: throw HttpException(404, "not found account")
        val responseUser = ResponseUser(account.id!!, account.username, account.nickname)
        return AuthenticationToken.successToken(
            accountDetails.username,
            accountDetails.authorities,
            responseUser,
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == AuthenticationToken::class.java
    }
}