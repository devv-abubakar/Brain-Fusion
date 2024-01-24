package devv.abubakar.brainfusion.model

class Level(
    val uId: String,
    val number: Int,
    val status: String,
    val score: Int
) {
    // Add a public no-argument constructor
    constructor() : this("",0, "", 0)
}
