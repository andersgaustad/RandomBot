package commandlogic

import com.jessecorbett.diskord.api.model.Message
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*
import kotlin.system.exitProcess

class Calculator : Command() {
    override val name: String
        get() = "calculate"

    override fun parseMessage(message: Message): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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


