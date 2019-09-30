package commandlogic

import com.jessecorbett.diskord.api.model.Message
import kotlin.math.*

class Calculator : Command() {
    override val name: String
        get() = "calculate"

    override fun parseMessage(message: Message): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

// Different interfaces used by calculator
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



