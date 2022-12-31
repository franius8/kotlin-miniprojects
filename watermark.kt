import java.io.File
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun main() {
   println("Input the image filename:")
    val fileName = readln()
    val image:BufferedImage
    try {
        val imageFile = File(fileName)
        image = ImageIO.read(imageFile)
    } catch (e: Exception) {
        println("The file $fileName doesn't exist.")
        return
    }
    if (image.colorModel.numColorComponents != 3) {
        println("The number of image color components isn't 3.")
        return
    }
    if (!(image.colorModel.pixelSize == 24 || image.colorModel.pixelSize == 32)) {
        println("The image isn't 24 or 32-bit.")
        return
    }
    println("Input the watermark image filename:")
    val watermarkFileName = readln()
    val watermark:BufferedImage
    try {
        val watermarkFile = File(watermarkFileName)
        watermark = ImageIO.read(watermarkFile)
    } catch (e: Exception) {
        println("The file $watermarkFileName doesn't exist.")
        return
    }
    if (watermark.colorModel.numColorComponents != 3) {
        println("The number of watermark color components isn't 3.")
        return
    }
    if (!(watermark.colorModel.pixelSize == 24 || watermark.colorModel.pixelSize == 32)) {
        println("The watermark isn't 24 or 32-bit.")
        return
    }
    if (image.height < watermark.height || image.width < watermark.width) {
        println("The watermark's dimensions are larger.")
        return
    }
    val alpha:Boolean = if (watermark.transparency == 3) {
        println("Do you want to use the watermark's Alpha channel?")
        readln() == "yes"
    } else {
        false
    }
    var transparentColor:Color? = null
    if (watermark.transparency != 3) {
        println("Do you want to set a transparency color?")
        if (readln() == "yes") {
            println("Input a transparency color ([Red] [Green] [Blue]):")
            val transparency = readln().split(" ").map { e -> e.toIntOrNull() }
            if (transparency.size != 3) {
                println("The transparency color input is invalid.")
                return
            }
            for (color in transparency) {
                if (color == null || color !in 0..255) {
                    println("The transparency color input is invalid.")
                    return
                }
            }
            transparentColor = Color(transparency[0]!!.toInt(), transparency[1]!!.toInt(), transparency[2]!!.toInt())
        }
    }
    println("Input the watermark transparency percentage (Integer 0-100):")
    val weight = readln().toIntOrNull()
    if (weight == null) {
        println("The transparency percentage isn't an integer number.")
        return
    }
    if (weight !in 0..100) {
        println("The transparency percentage is out of range.")
        return
    }
    println("Choose the position method (single, grid):")
    val positionMethod = readln()
    var positionSingle = listOf<Int?>(0, 0)
    when (positionMethod) {
        "single" -> {
            val xLimit = image.width - watermark.width
            val yLimit = image.height - watermark.height
            println("Input the watermark position ([x 0-${xLimit}] [y 0-${yLimit}]):")
            positionSingle = readln().split(" ").map { e -> e.toIntOrNull() }
            if (positionSingle.contains(null)) {
                println("The position input is invalid.")
                exitProcess(0)
            }
            if (positionSingle[0] !in 0..xLimit || positionSingle[1] !in 0..yLimit) {
                println("The position input is out of range.")
                exitProcess(0)
            }
        }
        "grid" -> {}
        else -> {
            println("The position method input is invalid.")
            return
        }
    }
    println("Input the output image filename (jpg or png extension):")
    val output = readln()
    if (output.takeLast(4) != ".jpg" && output.takeLast(4) != ".png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        return
    }
    val outputFile = File(output)
    val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val i = Color(image.getRGB(x, y))
            val w: Color = if (positionMethod == "single") {
                try {
                    Color(watermark.getRGB(x - positionSingle[0]!!.toInt(), y - positionSingle[1]!!.toInt()), alpha)
                } catch (e: Exception) {
                    outputImage.setRGB(x, y, i.rgb)
                    continue
                }
            } else {
                Color(watermark.getRGB(x % watermark.width, y % watermark.height), alpha)
            }
            if ((alpha && w.alpha == 0) || (w == transparentColor)) {
                outputImage.setRGB(x, y, i.rgb)
            } else {
                val color = Color(
                    (weight * w.red + (100 - weight) * i.red) / 100,
                    (weight * w.green + (100 - weight) * i.green) / 100,
                    (weight * w.blue + (100 - weight) * i.blue) / 100
                )
                outputImage.setRGB(x, y, color.rgb)
            }
        }
    }
    ImageIO.write(outputImage, output.takeLast(3), outputFile)
    println("The watermarked image $output has been created.")
}
