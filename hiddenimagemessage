import java.awt.image.BufferedImage
import java.awt.Color
import javax.imageio.ImageIO
import java.io.File
import java.lang.Exception

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val choice = readln()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> {
                println("Bye!")
                break
            }

            else -> println("Wrong task: $choice")
        }
    }
}

fun hide() {
    println("Input image file:")
    val input = readln()
    println("Output image file:")
    val output = readln()
    println("Message to hide:")
    val message = readln()
    println("Password:")
    val password = readln()
    val image: BufferedImage
    try {
        val imageFile = File(input)
        image = ImageIO.read(imageFile)
    } catch (e: Exception) {
        println("Can't read input file!")
        return
    }
    if (image.width * image.height < message.length * 8) {
        println("The input image is not large enough to hold this message.")
        return
    }
    val convertedMessage = (message).toByteArray()
    val convertedPassword = (password).toByteArray()
    val binaryMessage = convertedMessage.mapIndexed() {
            i, e -> Integer.toBinaryString(e.toInt() xor convertedPassword[i % convertedPassword.size].toInt())
                .padStart(8, '0')
    }
    val binaryString = binaryMessage.joinToString("") + "000000000000000000000011"
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val coordinate = y * image.width + x
            if (coordinate < binaryString.length) {
                val bit = binaryString[coordinate].toString().toInt()
                val pixel = Color(image.getRGB(x, y))
                val r = pixel.red
                val g = pixel.green
                val b = pixel.blue and 254 or bit % 256
                image.setRGB(x, y, Color(r, g, b).rgb)
            }
        }
    }
    val outputFile = File(output)
    ImageIO.write(image, "png", outputFile)
    println("Message saved in $output image.")
}

fun show() {
    println("Input image file:")
    val input = readln()
    println("Password:")
    val password = readln()
    val image: BufferedImage
    try {
        val imageFile = File(input)
        image = ImageIO.read(imageFile)
    } catch (e: Exception) {
        println("Can't read input file!")
        return
    }
    val convertedPassword = (password).toByteArray()
    var message = ""

    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val pixel = Color(image.getRGB(x, y))
            if (message.takeLast(24) == "000000000000000000000011") {
                break
            }
            message += Integer.toBinaryString(pixel.blue and 1)
        }
    }
    val decodedMessage = message.chunked(8).mapIndexed {
            i, e -> (e.toInt(2) xor convertedPassword[i % convertedPassword.size].toInt()).toChar() }.joinToString("").dropLast(3)
    println("Message:")
    println(decodedMessage)
}
