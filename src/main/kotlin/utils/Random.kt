package utils

import java.util.concurrent.ThreadLocalRandom

fun getRandomStringInArray(array : Array<String>) : String =
    array[ThreadLocalRandom.current().nextInt(0, array.size)]

fun <T> getRandomStringInArrayList(arrayList : ArrayList<T>) : T =
    arrayList[ThreadLocalRandom.current().nextInt(0, arrayList.size)]
