package com.clicks.app.feature.auth.data

import com.clicks.app.feature.auth.data.dto.toDomain
import com.clicks.app.feature.auth.model.User
import com.clicks.app.network.ApiClient
import com.clicks.app.network.ApiResult
import com.clicks.app.network.safeApiCall

class UserRepository(
    private val userApi: UserApi = ApiClient.createService<UserApi>()
) {
    suspend fun getCurrentUser(): ApiResult<User> {
        val result = safeApiCall {
            userApi.getCurrentUser()
        }
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(), result.message)
            is ApiResult.Error -> result
        }
    }
}
