package devv.abubakar.brainfusion.model

class Question(
    val correctOption: String,
    val level: Int,
    val number: String,
    val question: String,
    val option: String
) {
    constructor():this("",0,"","","")
}