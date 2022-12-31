fun main() {
    var boardString = "         "
    println("---------")
    println("| ${boardString[0]} ${boardString[1]} ${boardString[2]} |")
    println("| ${boardString[3]} ${boardString[4]} ${boardString[5]} |")
    println("| ${boardString[6]} ${boardString[7]} ${boardString[8]} |")
    println("---------")
    var xturn = true
    while (true) {
        var coordinates = listOf<String>()
        var index: Int
        while(true) {
          val move = readln()
            coordinates = move.split(" ")
            index = (coordinates[1].toInt() - 1) + ((coordinates[0].toInt() - 1) * 3)
            if (move.replace(" ", "").toIntOrNull() == null) {
            println("You should enter numbers!")
        } else {
          if (coordinates[0].toInt() > 3 || coordinates[1].toInt() > 3) {
            println("Coordinates should be from 1 to 3!")
        } else if (boardString[index] != ' ') {
            println("This cell is occupied! Choose another one!")
        } else {
              break
        } 
        }
        }
            val currentMarker: Char  
            if (xturn) currentMarker = 'X' else currentMarker = 'O'
              boardString = boardString.substring(0, index) + currentMarker + boardString.substring(index + 1)
            println("---------")
            println("| ${boardString[0]} ${boardString[1]} ${boardString[2]} |")
            println("| ${boardString[3]} ${boardString[4]} ${boardString[5]} |")
            println("| ${boardString[6]} ${boardString[7]} ${boardString[8]} |")
            println("---------")      
        xturn = !xturn
        val WINNING_COMBINATIONS = mutableListOf(
        mutableListOf<Int>(1, 2, 3), 
        mutableListOf<Int>(4, 5, 6), 
        mutableListOf<Int>(7, 8, 9), 
        mutableListOf<Int>(1, 4, 7), 
        mutableListOf<Int>(2, 5, 8), 
        mutableListOf<Int>(3, 6, 9), 
        mutableListOf<Int>(1, 5, 9), 
        mutableListOf<Int>(3, 5, 7))
    var xwon = false
    var owon = false
        var board = boardString.split("")
    for (combination in WINNING_COMBINATIONS) {
        var counterO = 0
        var counterX = 0
        for (i in combination) {
            if (board[i] == "X") {
                counterX++
            } else if (board[i] == "O") {
                counterO++
            }
        }
        if (counterX == 3) {
            xwon = true
        } else if (counterO == 3) {
            owon = true
        }
    }
    if (xwon && owon || boardString.count { it == 'X' } - boardString.count { it == 'O' } > 1 ||
            boardString.count { it == 'X' } - boardString.count { it == 'O' } < -1) {
        println("Impossible")
    } else if (xwon) {
        println("X wins")
        break
    } else if (owon) {
        println("O wins")
        break
    } else if (!board.contains(" ")) {
        println("Draw")
        break
    }
    }
}
