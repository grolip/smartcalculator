import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException
import java.lang.UnsupportedOperationException
import java.math.BigInteger
import java.util.Scanner

fun String.isDigit(): Boolean {
	return Regex("^[0-9]*$").containsMatchIn(this)
}

fun String.isLetter(): Boolean {
	return Regex("^[a-zA-Z]*$").containsMatchIn(this)
}

fun parseCommand(line: String): Boolean {
	var exit = false

	if (line == "/exit") {
		println("Bye!")
		exit = true
	} else if (line == "/help") {
		println("The program calculates the sum of numbers")
	} else {
		throw UnsupportedOperationException("Unknown command")
	}
	return exit
}

fun parseVar(line: String, cache: MutableMap<String, BigInteger>) {
	val (key, value) = line
		.replace(" ", "")
		.split('=', limit = 2)

	if (!Regex("^[a-zA-Z]*$").containsMatchIn(key))
		throw UnsupportedOperationException("Invalid identifier")

	if (value.isDigit())
		cache[key] = BigInteger(value)
	else if (cache.contains(value))
		cache[key] = cache.getValue(value)
	else if (value.isLetter())
		throw UnsupportedOperationException("Unknown variable")
	else
		throw UnsupportedOperationException("Invalid assignment")
}

fun main() {
	val scanner = Scanner(System.`in`)
	val cache = mutableMapOf<String, BigInteger>()
	var exit = false

	while (!exit) {
		val line = scanner.nextLine()
		val leftBracket = line.count { if (it == '(') true else false }
		val rightBracket = line.count { if (it == ')') true else false }

		try {
			if (!line.isNotBlank()) {
				continue
			} else if (line[0] == '/') {
				exit = parseCommand(line)
			} else if (cache.containsKey(line)) {
				println(cache.get(line))
			} else if (line.contains('=')) {
				parseVar(line, cache)
			} else if (Regex("^[\\ 0-9]*$").containsMatchIn(line)) {
				throw NumberFormatException()
			} else if (leftBracket != rightBracket) {
				throw NumberFormatException()
			} else {
				println(SuperOps(line).resolve(cache))
			}
		} catch (e: NumberFormatException) {
			println("Invalid expression")
		} catch (e: IndexOutOfBoundsException) {
			println("Invalid expression")
		} catch (e: UnsupportedOperationException) {
			println(e.message)
		}
	}
}
