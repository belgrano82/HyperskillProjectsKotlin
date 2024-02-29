fun main() {

    while (true) {
        println("Please input operation (encode/decode/exit):")
        when (val task = readln()) {
            "encode" -> {
                println("Input string:")
                val stringForEncoding = readln()
                encode(stringForEncoding)
            }

            "decode" -> {
                println("Input encoded string:")
                val decodedString = readln()
                decoded(decodedString)
            }

            "exit" -> {
                println("Bye!")
                break
            }

            else -> println("There is no '$task' operation")
        }
        println()
    }

}

private fun encode(stringForEncoding: String) {
    var binaryString = ""

    for (char in stringForEncoding) {
        binaryString += Integer.toBinaryString(char.code).padStart(7, '0')
    }

    val chuckNorrisCode = mutableListOf<String>()
    var position: Int

    while (binaryString.isNotEmpty()) {
        position = binaryString.indexOfFirst { it != binaryString.first() }
        if (position != -1) {
            chuckNorrisCode.add(binaryString.slice(0..<position))
            binaryString = binaryString.subSequence(position, binaryString.lastIndex + 1).toString()
        } else {
            chuckNorrisCode.add(binaryString)
            binaryString = ""
        }
    }

    println("Encoded string:")
    chuckNorrisCode.forEach {
        if (it.first() == '1') {
            print("0 ${it.replace('1', '0')} ")
        } else {
            print("00 $it ")
        }
    }
    println()
}


private fun decoded(codePhrase: String) {
    var result = ""
    var encodedString = codePhrase

    if (codePhrase.filter { it == ' ' }.length % 2 == 0 || codePhrase.filter { it != ' ' }.any { it != '0' }) {
        println("Encoded string is not valid.")
    } else {

        var binaryCode = ""
        var separator: Int

        while (encodedString.isNotEmpty()) {

            separator = getSeparator(encodedString)
            val code = encodedString.subSequence(0, separator).toString()

            if (code != "0" && code != "00") {
                result = "finish"
                break
            }

            encodedString = encodedString.subSequence(separator + 1, encodedString.lastIndex + 1).toString()
            separator = getSeparator(encodedString)

            if (separator != encodedString.lastIndex) {

                binaryCode += if (code.length == 1) {
                    encodedString.subSequence(0, separator).toString().replace('0', '1')
                } else {
                    encodedString.subSequence(0, separator).toString()
                }
            } else {
                binaryCode += if (code.length == 1) {
                    encodedString.replace('0', '1')
                } else {
                    encodedString
                }
                break
            }
            encodedString = encodedString.subSequence(separator + 1, encodedString.lastIndex + 1).toString()
        }
        val bytesArray = binaryCode.chunked(7).toMutableList()

        if (result == "finish" || bytesArray.any { it.length != 7 }){
            println("Encoded string is not valid.")
        } else {
            val decodedString = mutableListOf<Char>()
            for (symbol in bytesArray) {
                decodedString.add(symbol.toInt(2).toChar())
            }

            println("Decoded string:")
            decodedString.forEach { print(it) }
            println()
        }
    }
}

private fun getSeparator(decodedString: String) =
    if (decodedString.indexOfFirst { it == ' ' } != -1) decodedString.indexOfFirst { it == ' ' } else decodedString.lastIndex


