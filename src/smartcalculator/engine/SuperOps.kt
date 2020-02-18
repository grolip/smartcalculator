import java.lang.UnsupportedOperationException
import java.math.BigInteger

class SuperOps(val expression: String) {
	val infixExp: MutableList<String> = expression
		.replace(Regex("\\+{1,}"), "+")
		.replace(Regex("\\-{2,}")) { if (it.value.length % 2 == 0) "+" else "-" }
		.replace("-", " - ")
		.replace("+", " + ")
		.replace("*", " * ")
		.replace("/", " / ")
		.replace("^", " ^ ")
		.replace("(", "( ")
		.replace(")", " )")
		.replace(Regex("\\ {2,}"), " ")
		.split(" ")
		.toMutableList()

	val postfixExp = mutableListOf<String>()
	val stack = mutableListOf<String>()

	val operators = listOf<String>("+", "-", "/", "*", "^")
	val brackets = listOf<String>("(", ")")

	fun getPriority(token: String): Int {
		return if (token in operators.subList(0, 2)) {
			1
		} else if (token in operators.subList(2, 4)) {
			2
		} else if (token == operators[4]) {
			3
		} else {
			0
		}
	}

	fun processRightBracket() {
		for (ind in stack.lastIndex downTo 0) {
			if (stack[ind] == brackets[0]) {
				stack.removeAt(ind)
				break
			} else {
				postfixExp.add(stack[ind])
				stack.removeAt(ind)
			}
		}
	}

	fun processOperator(token: String) {
		val tokenPriority = this.getPriority(token)

		if (tokenPriority <= getPriority(stack.last())) {
			for (ind in stack.lastIndex downTo 0) {
				val curStackPriority = getPriority(stack[ind])

				if (stack[ind] == brackets[0] || tokenPriority > curStackPriority) {
					break
				} else {
					postfixExp.add(stack[ind])
					stack.removeAt(ind)
				}
			}
		}
		stack.add(token)
	}

	fun toPostfix() {
		for (token in infixExp) {
			if (token.isDigit()) {
				postfixExp.add(token)
			} else if (!stack.isNotEmpty() || stack.last() == brackets[0]) {
				stack.add(token)
			} else if (token in brackets) {

				when (token) {
					brackets[0] -> stack.add(token)
					else -> this.processRightBracket()
				}
			} else if (token in operators) {
				this.processOperator(token)
			}
		}
		postfixExp.addAll(stack.reversed())
	}

	fun applyOperator(first: BigInteger, other: BigInteger, op: String): BigInteger {
		return when (op) {
			"^" -> first.pow(other.toInt())
			"/" -> first / other
			"*" -> first * other
			"-" -> first - other
			else -> first + other
		}
	} 

	fun convertVar(cache: MutableMap<String, BigInteger>) {
		for (ind in 0..infixExp.lastIndex) {
			if (!infixExp[ind].isLetter())
				continue

			if (cache.containsKey(infixExp[ind])) {
				infixExp.set(ind, "${cache.get(infixExp[ind])}")
			} else {
				throw UnsupportedOperationException("Unknown variable")
			}
		}
	}

	fun resolve(cache: MutableMap<String, BigInteger>): BigInteger {
		val numbers = mutableListOf<BigInteger>()

		this.convertVar(cache)
		this.toPostfix()

		for (token in postfixExp) {
			if (token in operators) {
				val first = numbers[numbers.size - 2]
				val other = numbers.last()

				repeat(2) { numbers.removeAt(numbers.lastIndex) }
				numbers.add(this.applyOperator(first, other, token))
			} else {
				numbers.add(BigInteger(token))
			}
		}
		if (numbers.isNotEmpty())
			return numbers.last()
		return BigInteger("0")
	}
}
