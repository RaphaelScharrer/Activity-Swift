package at.htl.activitiy_android.domain.model

data class Word(
    val id: Long? = null,
    val word: String,
    val definition: String,
    val points: Int
)