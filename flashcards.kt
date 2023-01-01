import java.io.File
import java.lang.Exception
import kotlin.random.Random

data class Card(val term:String, val definition: String, var errors: Int = 0)

val flashcardsList = mutableListOf<Card>()
val log = mutableListOf<String>()
var exportOnExit: Boolean = false
lateinit var exportName:String

fun main(args: Array<String>) {
    if (args.contains("-import")) {
        importCards(args[args.indexOf("-import") + 1])
    }
    if (args.contains("-export")) {
        exportOnExit = true
        exportName = args[args.indexOf("-export") + 1]
    }
    main()
}

fun main() {
    while (true) {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readln()) {
            "add" -> addCard()
            "remove" -> removeCard()
            "import" -> importCards()
            "export" -> exportCards()
            "ask" -> askUser()
            "log" -> exportLog()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
            "exit" -> {
                println("Bye bye!")
                if (exportOnExit) {
                    exportCards(exportName)
                }
                return
            }
        }
    }
}

fun addCard() {
    println("The card:")
    val key = readln()
    if (flashcardsList.any { it.term == key }) {
       println("The card \"$key\" already exists.")
        return
    }
    println("The definition of the card:")
    val definition = readln()
    if (flashcardsList.any { it.definition == definition }) {
        println("The definition \"$definition\" already exists.\n")
        return
    }
    flashcardsList.add(Card(key, definition))
    println("The pair (\"$key\":\"$definition\") has been added.")
}

fun removeCard() {
    println("Which card?")
    val toRemove = readln()
    if (flashcardsList.any { it.term == toRemove }) {
        flashcardsList.removeIf { it.term == toRemove }
        println("The card has been removed.")
    } else {
        println("Can't remove \"$toRemove\": there is no such card.")
    }
}

fun importCards(name: String? = null) {
    val fileName = if (name != null) {
        name
    } else {
        println("File name:")
        readln()
    }
    val file:File
    val lines: List<String>
    try {
        file = File(fileName)
        lines = file.readLines()

    } catch (e: Exception) {
        println("File not found.")
        return
    }
    for (line in lines) {
        val (term, definition, errors) = line.split("%%")
        if (flashcardsList.any { it.term == term }) {
            flashcardsList.removeIf { it.term == term }
        }
        flashcardsList.add(Card(term, definition, errors.toInt()))
    }
    println("${lines.size} cards have been loaded.")
}

fun exportCards(name: String? = null) {
    val fileName = if (name != null) {
        name
    } else {
        println("File name:")
        readln()
    }
    val file = File(fileName)
    file.writeText("")
   for (card in flashcardsList) {
       file.appendText("${card.term}%%${card.definition}%%${card.errors}\n")
   }
    println("${flashcardsList.size} cards have been saved.")
}

fun askUser() {
    println("How many times to ask?")
    val times = readln().toInt()
    for (i in 1..times) {
        val number: Int = if (flashcardsList.size == 1) {
            0
        } else {
            Random.nextInt(0, flashcardsList.size - 1)
        }
        val card = flashcardsList[number]
        println("Print the definition of \"${card.term}\":")
        val guess = readln()
        if (guess == card.definition) {
            println("Correct!")
        } else if (flashcardsList.any { it.definition == guess }) {
            val correctTerm = flashcardsList.find { it.definition == guess }!!.term
            println("Wrong. The right answer is \"${card.definition}\", but your " +
                    "definition is correct for \"$correctTerm\"")
            card.errors += 1
        } else {
            println("Wrong. The right answer is \"${card.definition}\".")
            card.errors += 1
        }
    }
}

fun exportLog() {
    println("File name:")
    val fileName = readln()
    val file = File(fileName)
    file.writeText("")
    for (line in log) {
        file.appendText("$line\n")
    }
    println("The log has been saved.")
}

fun resetStats() {
    for (card in flashcardsList) {
        card.errors = 0
    }
    println("Card statistics have been reset.")
}

fun hardestCard() {
    val hardestCards = mutableListOf<Card>()
    var highest = 0
    for (card in flashcardsList) {
        if (card.errors > highest) {
            hardestCards.clear()
            hardestCards.add(card)
            highest = card.errors
        } else if (card.errors == highest && highest != 0) {
            hardestCards.add(card)
        }
    }
    when (hardestCards.size) {
        0 -> {
            println("There are no cards with errors.")
        }
        1 -> {
            val card = hardestCards[0]
            println("The hardest card is \"${card.term}\". You have ${card.errors} errors answering it")
        }
        else -> {
            val names = mutableListOf<String>()
            for (card in hardestCards) {
                names.add(card.term)
            }
            println("The hardest cards are \"${names.joinToString("\", \"")}\". " +
                    "You have ${hardestCards[0].errors} errors answering them.")
        }
    }
}

fun println(str: String) {
    log.add(str)
    kotlin.io.println(str)

}

fun readln(): String {
    val input = kotlin.io.readln()
    log.add("> $input")
    return input
}
