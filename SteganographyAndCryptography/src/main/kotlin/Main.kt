import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val str = readln()) {
            "hide" -> {
                hideImage()

            }

            "show" -> {
                showImage()

            }

            "exit" -> {
                println("Bye!")
                break
            }

            else -> println("Wrong input: $str")
        }
    }
}


private fun hideImage() {
    println("Input image file:")
    val inputImagePath = readln()
    println("Output image file:")
    val outputImagePath = readln()
    println("Message to hide:")
    val message = readln()
    println("Password:")
    val password = readln()
    try {
        changeImage(inputImagePath, outputImagePath, message, password)
        println(
            """
                    Input Image: $inputImagePath
                    Output Image: $outputImagePath
                    Image $outputImagePath is saved.
                """.trimIndent()
        )
    } catch (e: Exception) {
        println(e.message)
    }

}

private fun showImage() {
    println("Input image file:")
    val fileName = readln()
    println("Password:")
    val password = readln()
    val cryptoMess = extractEncryptedBits(fileName)
    val cryptoPass = passwordToBits(password)
    println("Message:")
    println(decryptMessage(cryptoMess, cryptoPass))
}

private fun changeImage(imagePath: String, outputImagePath: String, message: String, password: String): File {
    val messageBytes = message.toByteArray(Charsets.UTF_8)
    val bitsets = ArrayList<String>()

    val passwordBytes = password.toByteArray(Charsets.UTF_8)
    val passBitSets = ArrayList<String>()

    for (byte in messageBytes) {
        bitsets.add(byte.toInt().and(0xFF).toString(2).padStart(8, '0'))
    }
    for (byte in passwordBytes) {
        passBitSets.add(byte.toInt().and(0xFF).toString(2).padStart(8, '0'))
    }

    val cryptoMess = bitsets.flatMap { it.toList() }.map { Character.getNumericValue(it) }
    val cryptoPass = passBitSets.flatMap { it.toList() }.map { Character.getNumericValue(it) }.toMutableList()
    val size = cryptoMess.size

    while (cryptoPass.size < cryptoMess.size) {
        for (i in 0 until size) {
            cryptoPass.add(cryptoPass[i])
        }
        if (cryptoPass.size > cryptoMess.size) {
            val d = cryptoPass.size - cryptoMess.size
            repeat(d) {
                cryptoPass.removeLast()
            }
        }
    }

    val encryptedMessage = List(cryptoMess.size) { 0 }.toMutableList()
    val endOfEncryptedMessage = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1)

    for (i in cryptoMess.indices) {
        encryptedMessage[i] = cryptoMess[i] xor cryptoPass[i]
    }

    val encrypted = encryptedMessage + endOfEncryptedMessage
    val inputFile = File(imagePath)
    val image = ImageIO.read(inputFile)

    if (encrypted.size > image.height * image.height) {
        println("The input image is not large enough to hold this message.")
        return inputFile
    } else {
        var bitIndex = 0

        loop@ for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                if (bitIndex >= encrypted.size) break@loop

                val color = Color(image.getRGB(x, y))
                val newBlue = color.blue and 254 or encrypted[bitIndex]
                image.setRGB(x, y, Color(color.red, color.green, newBlue).rgb)

                bitIndex++
            }
        }

        val outputFile = File(outputImagePath)
        ImageIO.write(image, "png", outputFile)
        println("Message saved in $outputImagePath image.")
        return outputFile
    }

}

fun extractEncryptedBits(imagePath: String): List<Int> {
    val inputFile = File(imagePath)
    val image = ImageIO.read(inputFile)
    val bits = mutableListOf<Int>()

    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val color = Color(image.getRGB(x, y))
            val bit = color.blue and 1
            bits.add(bit)
        }
    }

    val endMarker = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1)
    val endIndex = findFirstIndexOfSlice(bits, endMarker) ?: bits.size
    return bits.take(endIndex)
}

fun findFirstIndexOfSlice(bits: List<Int>, slice: List<Int>): Int? {
    for (index in 0..bits.size - slice.size) {
        if (bits.subList(index, index + slice.size) == slice) {
            return index
        }
    }
    return null
}

fun passwordToBits(password: String): List<Int> {
    val passwordBytes = password.toByteArray(Charsets.UTF_8)
    val passBitSets = mutableListOf<Int>()

    for (byte in passwordBytes) {
        val bits = byte.toInt().and(0xFF).toString(2).padStart(8, '0') // �������������� � �������� ������
        for (bit in bits) {
            passBitSets.add(bit.toString().toInt())
        }
    }

    return passBitSets
}

fun decryptMessage(encryptedBits: List<Int>, passwordBits: List<Int>): String {
    val repeatedPasswordBits = if (passwordBits.size < encryptedBits.size) {
        passwordBits.toMutableList().apply {
            while (size < encryptedBits.size) {
                addAll(passwordBits.take(encryptedBits.size - size))
            }
        }
    } else {
        passwordBits
    }

    val decryptedBits = encryptedBits.mapIndexed { index, bit ->
        bit xor repeatedPasswordBits[index]
    }

    return bitsToString(decryptedBits)
}

fun bitsToString(bits: List<Int>): String {
    return bits.chunked(8).joinToString(separator = "") { byteBits ->
        val byte = byteBits.joinToString("").toInt(2)
        byte.toChar().toString()
    }
}