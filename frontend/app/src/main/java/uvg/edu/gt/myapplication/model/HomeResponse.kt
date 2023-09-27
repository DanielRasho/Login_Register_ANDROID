package uvg.edu.gt.myapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse (
    val message: String,
    val timeRemaining: Int
)
