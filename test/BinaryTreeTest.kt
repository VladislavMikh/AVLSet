import org.junit.Test
import org.junit.Assert.*
import java.util.*

class BinaryTreeTest {
    @Test
    fun testRemove() {
        val random = Random()
        for (iteration in 1..1000) {
            println("----------$iteration----------")
            val list = mutableListOf<Int>()
            for (i in 1..20) {
                list.add(random.nextInt(100))
            }
            val treeSet = TreeSet<Int>()
            val binarySet = AVLSet<Int>()
            for (element in list) {
                treeSet += element
                binarySet += element
            }
            val toRemove = list[random.nextInt(list.size)]
            println("Removing $toRemove from ${list.sorted()}")
            treeSet.remove(toRemove)
            binarySet.remove(toRemove)
            println(treeSet)
            println("[${binarySet.joinToString(separator = ", ")}]")
            assertEquals("After removal of $toRemove from $list", treeSet, binarySet)
            assertEquals(treeSet.size, binarySet.size)
            for (element in list) {
                val inn = element != toRemove
                assertEquals("$element should be ${if (inn) "in" else "not in"} tree", inn, element in binarySet)
            }
        }
    }
}