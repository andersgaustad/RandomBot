package messageevents

open class Node(private val caption : String, val options : List<String>, private val bulletFormat : String = "- ") {

    val children = ArrayList<Node>(options.size)

    fun attach(child : Node) {
        children.add(child)
    }

    fun getChild(index : Int) = children[index]

    open fun getDisplayMessage() = caption + "\n" + getOptionDisplay()

    private fun getOptionDisplay() : String {
        val sb = StringBuilder()
        sb.append("```\n")
        sb.append(options.joinToString("\n" + bulletFormat, bulletFormat))
        sb.append("\n```")

        return sb.toString()
    }

    fun isLeafNode() = children.isEmpty()

}

class HyperlinkLeafNode(val name: String, private val hyperlink : String) : Node(name, arrayListOf()) {
    override fun getDisplayMessage() = name + "\n" + hyperlink
}