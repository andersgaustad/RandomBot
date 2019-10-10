package commandlogic

import com.jessecorbett.diskord.api.model.Message
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
    "log" to Log10Function(),
    "log2" to Log2Function()
)

val operatorMap = mapOf(
    "+" to Add(),
    "-" to Sub(),
    "*" to Mul(),
    "/" to Div()
)


class Calculator : Command() {
    override val name: String
        get() = "calculate"

    override fun parseMessage(message: Message): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun buildSubSafeString(raw : String) : String {
        val sb = StringBuilder()

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
                    sb.append("(0-")
                    for (j in i until raw.length) {
                        // There may be more negative numbers nested in here, but these wil be solved later
                        val nestedChar = raw[j].toString()

                        if (operatorMap.keys.contains(nestedChar)) {
                            // Should now have found operator and end of (0-x)
                            sb.append(")")
                            // Add the rest of the string to the builder
                            sb.append(raw.substring(j))

                            // We have now created a slightly more correct string
                            // Return this instead, adn fix recursively
                            break

                        } else {
                            sb.append(nestedChar)
                        }
                    }
                    // Return after breaking or getting out of the loop
                    return buildSubSafeString(sb.toString())

                } else {
                    sb.append(currentChar)
                }
            }
        }

        // If no errors are found simply return the built string
        return sb.toString()
    }

    // Build safe string:
    val safe = buildSubSafeString(raw)

    // Regexes to match

    // Ints
    val intPattern = Regex("^[0 | [123456789]+]")

    // Parenthesis
    val parenthesisPattern = Regex("^[(|)]")

    val functionPattern = Regex("^[" + functionMap.keys.joinToString("|") + "]")
    val operatorPattern = Regex("^[" + operatorMap.keys.joinToString("|") + "]")

    val result = ArrayList<Any>()

    // TODO


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




