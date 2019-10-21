package commandlogic

import com.jessecorbett.diskord.api.model.Message
import com.jessecorbett.diskord.util.words
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*
import kotlin.text.StringBuilder

val functionMap = mapOf(
    "sin" to SinFunction(),
    "cos" to CosFunction(),
    "sqrt" to SqrtFunction(),
    "exp" to ExpFunction(),
    "ln" to NaturalLogarithmFunction(),
    "log2" to Log2Function(),
    "log" to Log10Function()
)

val operatorMap = mapOf(
    "+" to Add(),
    "\\-" to Sub(),
    "*" to Mul(),
    "/" to Div()
)

/*
val arithmeticMap =  mapOf(
    "+" to "ADD",
    "-" to "MIN",
    "*" to "MUL",
    "/" to "DIV"
)

 */


class Calculator : Command() {
    override val name: String
        get() = "calculate"

    override fun parseMessage(message: Message): String {
        val words = message.words.subList(1, message.words.size)

        // Parse a list with types
        println("Parsing")
        val parsed = parseString(words.joinToString(""))
        // Translate list to RPN
        println("Translate")
        val rpn = shuntingYardTranslate(parsed)
        // Evaluate and return result
        println("Evaluate")
        return evaluateRPN(rpn).toString()
    }

}


fun evaluateRPN(RPNQueue : ArrayList<Any>) : Double {
    // Stack needed for calculation
    val stack = Stack<Double>()

    fun handleFunction(function: Function) : Double =
        function.execute(stack.pop())

    fun handleOperator(operator: Operator) : Double {
        val op2 = stack.pop()
        val op1 = stack.pop()

        return operator.execute(op1, op2)
    }

    // Iterate through the queue
    for (item in RPNQueue) {
        // Switch on type
        when(item) {
            is Double -> stack.push(item)
            is Function -> stack.push(handleFunction(item))
            is Operator -> stack.push(handleOperator(item))
        }

    }

    return stack.peek()

}

fun shuntingYardTranslate(inputQueue : ArrayList<Any>) : ArrayList<Any> {
    val output = ArrayList<Any>()
    val stack = Stack<Any>()

    fun handleEndParenthesis() {
        var element = stack.pop()
        // Do until we find start parenthesis
        while (element != "(") {
            output.add(element)

            element = stack.pop()
        }

    }

    fun handleOperator(operator: Operator) {
        loop@ while (stack.isNotEmpty()) {
            // Checking top of the stack as long as we are not done
            when(val top = stack.peek()) {
                is Function -> output.add(stack.pop())
                is Operator -> {
                    if (top.strength >= operator.strength) {
                        output.add(stack.pop())

                    } else {
                        break@loop // Whoa
                    }
                }
                else -> break@loop // Whoa dude
            }

        }

        stack.push(operator)

    }

    // Iterate over input
    for (item in inputQueue) {
        // Switch on type
        when(item) {
            is Double -> output.add(item)
            is Int -> output.add(item.toDouble())
            is Function -> stack.add(item)
            "(" -> stack.add(item)
            ")" -> handleEndParenthesis()
            is Operator -> handleOperator(item)
            else -> throw IllegalArgumentException("How did you get here?")
        }
    }

    // When done, put the rest of stack in output
    while (stack.isNotEmpty()) {
        output.add(stack.pop())
    }

    return output
}

fun parseString(string: String) : ArrayList<Any> {
    val raw = string.replace(" ", "").toLowerCase()
    println("Raw: $raw")

    fun buildSubSafeString(raw : String) : String {
        val sb = StringBuilder()

        // Translate minuses to (0-x) when missing prefix
        fun parseMinus(raw : String) : String {
            val minusSB = StringBuilder()

            // Iterate through all indexes
            for (i in raw.indices) {
                val currentChar = raw[i]

                // Check if we hit a -
                if (currentChar == '-') {
                    // If we found a - without a numeric before it the user wants to input a negative number
                    // This should only happen when we start with a negative numner or after an opening parenthesis
                    // The parenthesis may be a function call or a regular set
                    if (i == 0 || raw[i-1] == '(') {
                        // We need to reformat string from -x to (0-x)
                        // The parenthesis should start immediately and continue until next operator
                        minusSB.append("(0")
                        for (j in i until raw.length) {
                            // There may be more negative numbers nested in here, but these wil be solved later
                            val nestedChar = raw[j].toString()

                            if (operatorMap.keys.contains(nestedChar)) {
                                // Should now have found operator and end of (0-x)
                                minusSB.append(")")
                                // Add the rest of the string to the builder
                                minusSB.append(raw.substring(j))

                                // We have now created a slightly more correct string
                                // Return this instead, adn fix recursively
                                break

                            } else {
                                minusSB.append(nestedChar)
                            }
                        }
                        // Return after breaking or getting out of the loop
                        return buildSubSafeString(minusSB.toString())

                    } else {
                        minusSB.append(currentChar)
                    }
                } else {
                    minusSB.append(currentChar)
                }
            }

            // If no errors are found simply return the built string
            println("Built : ${minusSB.toString()}")
            return minusSB.toString()
        }

        // Translate to a min safe string
        val processed = parseMinus(raw)

        // Iterate through all indexes
        for (i in processed.indices) {
            val currentChar = processed[i].toString()

            sb.append(currentChar)
        }

        return sb.toString()

    }

    // Build safe string:
    var regexString = buildSubSafeString(raw)

    // Regexes to match
    // Ints
    val intPattern = Regex("^[0-9]")

    // Parenthesis
    val parenthesisPattern = Regex("^[(|)]")

    // Operators
    val operatorPattern = Regex("^[(" + operatorMap.keys.joinToString(")|(") + ")]")

    // Functions
    val functionPattern = Regex("^(" + functionMap.keys.joinToString(")|(") + ")")


    val result = ArrayList<Any>()

    val patterns = arrayOf(intPattern, parenthesisPattern, operatorPattern, functionPattern)

    // Parse the actual string
    while (regexString.isNotEmpty()) {
        println("Regex: $regexString")
        for (pattern in patterns) {
            val check = pattern.find(regexString)
            println("Checking for $pattern: $check")
            if (check != null) {
                println("Value: ${check.value}")
                val toCheck = regexString.substring(check.range.first, check.range.last+1)
                println("Tested string: $toCheck (${check.range.first}, ${check.range.last+1})")
                when(pattern) {
                    intPattern -> {
                        // If we found a single digit int:
                        if(result.isNotEmpty()) {
                            when(result.last()) {
                                is Int -> {
                                    // Hmmmm, is there really not any simpler way?
                                    result[result.lastIndex] = result.last().toString().toInt() * 10 + check.value.toInt()

                                }
                                else -> result.add(check.value.toInt())
                            }
                        } else {
                            result.add(check.value.toInt())
                        }

                    }
                    parenthesisPattern -> result.add(check.value)
                    // Functions and operands should always be in map, but safe casting to be safe
                    functionPattern -> functionMap[check.value]?.let { result.add(it)}
                    operatorPattern -> {
                        // "-" needs to escape when used in regex, so we need special case
                        if (check.value == "-") {
                            operatorMap["\\" + check.value]?.let { result.add(it)}

                        } else {
                            // Normally we just add as normal
                            operatorMap[check.value]?.let { result.add(it)}
                        }
                    }
                }
                println("Added ${check.value}")

                // Reduce regex string
                regexString = regexString.substring(check.range.last+1)
                break

            }
        }

    }
    println("Parsed ${result.joinToString("|")}")
    return result

}

// Functions
interface Function {
    fun execute(double: Double) : Double

}

class SinFunction : Function {
    override fun execute(double: Double): Double {
        return sin(double)
    }

}

class CosFunction : Function {
    override fun execute(double: Double): Double {
        return cos(double)
    }

}

class SqrtFunction : Function {
    override fun execute(double: Double): Double {
        return sqrt(double)
    }

}

class ExpFunction : Function {
    override fun execute(double: Double): Double {
        return exp(double)
    }

}

class NaturalLogarithmFunction : Function {
    override fun execute(double: Double): Double {
        return ln(double)
    }

}

open class LogFunction(val base: Double) : Function {

    override fun execute(double: Double): Double {
        return log(base, double)
    }

}

class Log2Function : LogFunction(2.0)

class Log10Function : LogFunction(10.0)


// Operators
abstract class Operator(val strength : Int) {
    abstract fun execute(op1: Double, op2 : Double) : Double
}

class Add : Operator(0) {
    override fun execute(op1: Double, op2: Double): Double {
        return op1 + op2
    }

}

class Sub : Operator(0) {
    override fun execute(op1: Double, op2: Double): Double {
        return op1 - op2
    }

}

class Mul : Operator(1) {
    override fun execute(op1: Double, op2: Double): Double {
        return op1 * op2
    }

}

class Div : Operator(1) {
    override fun execute(op1: Double, op2: Double): Double {
        // Let kotlin manage zero division on its own
        try {
            op1 / op2
        } catch (ze: ArithmeticException) {
            ze.printStackTrace()

        } finally {
            return op1 / op2
        }
    }

}




