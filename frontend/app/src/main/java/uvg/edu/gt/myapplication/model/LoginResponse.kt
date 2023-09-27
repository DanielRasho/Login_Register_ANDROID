package uvg.edu.gt.myapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val message: String,
    val token: String,
)