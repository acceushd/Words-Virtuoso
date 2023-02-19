package wordsvirtuoso

import java.io.File
import kotlin.random.Random

val regex = "([A-Z]|[a-z]){5}".toRegex()
val regex2 = "^.{5}$".toRegex()
var wrongChars = mutableSetOf<Char>()
val list = mutableListOf<String>()

fun String.changeAzure() = "\u001B[48:5:14m$this\u001B[0m"
fun Char.changeGreen() = "\u001B[48:5:10m$this\u001B[0m"
fun Char.changeYellow() = "\u001B[48:5:11m$this\u001B[0m"
fun Char.changeGrey() = "\u001B[48:5:7m$this\u001B[0m"

fun wordle(secretWord: String, guessWord: String): String {
    var result = ""
    for (i in secretWord.indices) {
        if (secretWord[i] == guessWord[i]) {
            result += secretWord[i].uppercaseChar().changeGreen()
        } else if (secretWord.contains(guessWord[i])) {
            result += guessWord[i].uppercaseChar().changeYellow()
        } else {
            wrongChars += guessWord[i].uppercaseChar()
            result += guessWord[i].uppercaseChar().changeGrey()
        }
    }
    return result
}


fun checkLetters(input: String): Boolean {
    var used = ""
    for (i in input) {
        if (used.contains(i)) return true
        else used += i
    }
    return false
}

fun check(input: String): Boolean {
    if (!regex2.matches(input)) {
        println("The input isn't a 5-letter word.")
        return false
    }
    if (!input.matches("[a-zA-Z]+".toRegex())) {
        println("One or more letters of the input aren't valid.")
        return false
    }

    if (checkLetters(input)) {
        println("The input has duplicate letters.")
        return false
    }

    return true
}

fun check2(input: String): Boolean {
    if (!regex.matches(input)) {
        return false
    }
    if (!input.matches("[a-zA-Z]+".toRegex())) {
        return false
    }

    if (checkLetters(input)) {
        return false
    }

    return true
}

fun checkIfInFile(input: String, file: String): Boolean {
    val file = File(file).readLines()
    return file.contains(input)
}

fun checkFile(filePath: String): Pair<Boolean, Int> {
    val file = File(filePath)
    var valid = true
    var failed = 0
    file.forEachLine {
        if (!check2(it)) {
            valid = false
            failed += 1
        }
    }
    return Pair(valid, failed)
}

fun checkLines(file1: String, file2: String): Pair<Boolean, Int> {
    val content1O = File(file1).readLines()
    val content2O = File(file2).readLines()
    val content1 = content1O.map { it.lowercase() }
    val content2 = content2O.map { it.lowercase() }
    var number = 0
    var valid = true
    if (content2.size > content1.size) {
        val missing = content2.drop(content1.size)
        return Pair(false, missing.size)
    }
    for (i in content2) {
        if (!content1.contains(i)) {
            valid = false
            number += 1
        }
    }
    return Pair(valid, number)
}

fun main(args: Array<String>) {
    var tries = 0
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return
    }
    if (!File(args[0]).exists()) {
        println("Error: The words file ${args[0]} doesn't exist.")
        return
    } else if (!File(args[1]).exists()) {
        println("Error: The candidate words file ${args[1]} doesn't exist.")
        return
    }
    val check1 = checkFile(args[0])
    val check2 = checkFile(args[1])
    val files = checkLines(args[0], args[1])
    if (!check1.first) {
        println("Error: ${check1.second} invalid words were found in the ${args[0]} file.")
        return
    } else if (!check2.first) {
        println("Error: ${check2.second} invalid words were found in the ${args[1]} file.")
        return
    } else if (!files.first) {
        println("Error: ${files.second} candidate words are not included in the ${args[0]} file.")
        return
    }
    val file1 = File(args[1]).readLines()
    val number = Random.nextInt(file1.size)
    val secretWord = file1[number].uppercase()
    println("Words Virtuoso")
    val start = System.currentTimeMillis()
    here@ while (true) {
        tries += 1
        println("Input a 5-letter word:")
        val input = readln()
        if (input == "exit") {
            println("The game is over.")
            break
        }
        if (!check(input))
            continue@here
        if (!checkIfInFile(input, args[0])) {
            println("The input word isn't included in my words list.")
            continue@here
        }
        val check = wordle(secretWord, input.uppercase())
        if (input.lowercase() == secretWord.lowercase()) {
            println(list.joinToString("\n"))
            println(check.uppercase())
            println("Correct!")
            break
        }
        list.add(check)
        println(list.joinToString("\n"))
        println(wrongChars.sorted().joinToString("").changeAzure())
    }
    val end = System.currentTimeMillis()
    val time = end - start
    val seconds = time / 1000
    if (tries > 1) {
        println("The solution was found after $tries tries in $seconds seconds.")
    } else
        println("Amazing luck! The solution was found at once.")
}
