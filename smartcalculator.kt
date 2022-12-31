import java.math.BigInteger

fun main() {
    val variableMap = mutableMapOf<String, BigInteger>()
    mainLoop@ while (true) {
        val input = readln().trimEnd(' ').trimStart(' ')
        when {
            "^[A-Za-z]+$".toRegex().matches(input) -> when {
                variableMap.containsKey(input) -> println(variableMap[input])
                else -> println("Unknown variable")
            }
            "=".toRegex().containsMatchIn(input) -> {
                val splitInput = input.split("\\s*=\\s*".toRegex())
                when {
                    !"^[A-Za-z]+$".toRegex().matches(splitInput[0]) -> println("Invalid identifier")
                    splitInput.size > 2 -> println("Invalid assignment")
                    !"^[A-Za-z]+\$|^-?[0-9]+$".toRegex().matches(splitInput[1]) -> println("Invalid assignment")
                    splitInput[1].toBigIntegerOrNull() != null -> variableMap[splitInput[0]] = splitInput[1].toBigInteger()
                    ("^[A-Za-z]+$").toRegex().matches(splitInput[1]) -> {
                        if (variableMap.containsKey(splitInput[1])) {
                            val value = variableMap[splitInput[1]]!!
                           variableMap[splitInput[0]] = value
                        } else {
                            println("Unknown variable")
                        }
                    }
                }
            }
            "/.+".toRegex().matches(input) -> when(input) {
                "/exit" -> {
                    println("Bye!")
                    return
                }
                "/help" -> println("The program calculates the sum or difference of numbers")
                else -> println("Unknown command")
            }
            input == "" -> {}
            !"[a-zA-Z0-9+()*/ -]+".toRegex().matches(input) -> println("Invalid expression")
            input.toBigIntegerOrNull() != null -> println(input.toBigInteger())
            else -> {
                val expression = input.split("").filter { e -> "[A-Za-z0-9()+*/-]+".toRegex().matches(e)}
                val postFixResult = mutableListOf<String>()
                val stack = mutableListOf<String>()
                var number:String = ""
                var prev = ""
                for (element in expression) {
                    if ("[+*/-]".toRegex().matches(element) && prev == element) {
                        when (element) {
                            "+" -> continue
                            "-" -> {
                                if (stack.removeLast() == "-") stack.add("+") else stack.add("-")
                                continue
                            }
                            else -> {
                                println("Invalid expression")
                                continue@mainLoop
                            }
                        }
                    }
                    if (number != "" && !"[0-9]+".toRegex().matches(element)) {
                        postFixResult.add(number)
                        number = ""
                    }
                    when {
                        !"^-?[A-Za-z0-9]+$|^[+]+$|^-+$|^/$|^[*]$|^[(]$|^[)]\$".toRegex().matches(element) -> {
                            println("Invalid expression")
                            break
                        }
                        "[0-9]+".toRegex().matches(element) -> { number += element }
                        "[A-Za-z]+".toRegex().matches(element) -> {
                            when {
                                !variableMap.containsKey(element) -> {
                                    println("Unknown variable")
                                    return
                                }
                                else -> postFixResult.add(variableMap[element].toString())
                            }
                        }
                        stack.isEmpty() || stack.last() == "(" || element == "(" -> stack.add(element)
                        element == ")" -> {
                            while(true) {
                                when {
                                    stack.isEmpty() -> {
                                        println("Invalid expression")
                                        continue@mainLoop
                                    }
                                    stack.last() == "(" -> {
                                        stack.removeLast()
                                        break
                                    }
                                    else -> postFixResult.add(stack.removeLast())
                                }
                            }
                        }
                        precedence(element) > precedence(stack.last()) -> stack.add(element)
                        else -> {
                            while(true) {
                                when {
                                    stack.isEmpty() || stack.last() == "(" || precedence(stack.last()) < precedence(element) -> break
                                    else -> postFixResult.add(stack.removeLast())
                                }
                            }
                            stack.add(element)
                        }
                    }
                    prev = element
                }
                if (number != "") {
                    postFixResult.add(number)
                }
                while (stack.isNotEmpty()) {
                    postFixResult.add(stack.removeLast())
                }
                if (postFixResult.contains("(")) {
                    println("Invalid expression")
                    continue
                }
                val calculationStack = mutableListOf<String>()
                for (element in postFixResult) {
                    when {
                        "-?[0-9]+".toRegex().matches(element) -> calculationStack.add(element)
                        else -> {
                            val second = calculationStack.removeLast().toBigInteger()
                            val first = calculationStack.removeLast().toBigInteger()
                            when (element) {
                                "+" -> calculationStack.add((first + second).toString())
                                "-" -> calculationStack.add((first - second).toString())
                                "*" -> calculationStack.add((first * second).toString())
                                "/" -> calculationStack.add((first / second).toString())
                            }
                        }
                    }
                }
                println(calculationStack.last())

            }
        }  
    }
    
}

fun precedence(operand: String): Int {
    return when (operand) {
        "*" -> 1
        "/" -> 1
        else -> 0
    }
}
