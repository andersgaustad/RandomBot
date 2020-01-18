package messageevents

open class Node(private val caption : String, val options : List<String>) {

    val children = ArrayList<Node>(options.size)

    open fun attach(vararg nodes : Node) = nodes.forEach { children.add(it) }



    fun getChild(index : Int) = children[index]

    open fun getDisplayMessage() = caption + "\n" + getOptionDisplay()

    private fun getOptionDisplay() : String {
        val sb = StringBuilder()
        sb.append("```\n")
        options.forEachIndexed { index, s ->
            sb.append("(${index+1}) $s\n")
        }
        sb.append("```")

        return sb.toString()
    }

    fun isLeafNode() = children.isEmpty()

}

class LeafNode(val name: String, private val hyperlink : String) : Node(name, arrayListOf()) {
    override fun getDisplayMessage() = name + "\n" + hyperlink

    override fun attach(vararg nodes : Node) = throw IllegalAccessException("Cannot attach more nodes to leaves!")
}

class WayPointNode(nodes: List<LeafNode>) : Node("I found multiple animes that might interest you\nChoose one of the following", getWayPointOptions(nodes)) {
    fun attachLeafNodes(nodes: List<LeafNode>) = nodes.forEach { children.add(it) }
}


fun createWayPointNode(vararg nodes: LeafNode) : WayPointNode {
    val nodeList = nodes.asList()
    val node = WayPointNode(nodeList)
    node.attachLeafNodes(nodeList)
    return node
}

private fun getWayPointOptions(nodes: List<LeafNode>) : List<String> {
    val names = ArrayList<String>(nodes.size)
    nodes.forEach {
        names.add(it.name)
    }
    return names
}