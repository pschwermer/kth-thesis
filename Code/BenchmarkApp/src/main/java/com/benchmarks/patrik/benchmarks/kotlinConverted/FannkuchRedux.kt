package com.benchmarks.patrik.benchmarks.kotlinConverted

/* The Computer Language Benchmarks Game
   http://benchmarksgame.alioth.debian.org/

   contributed by Isaac Gouy
   converted to Java by Oleg Mazurov
   mostly auto-converted to Kotlin by Patrik Schwermer
*/

class FannkuchRedux {
    fun fannkuch(n: Int): Int {
        val perm = IntArray(n)
        val perm1 = IntArray(n)
        val count = IntArray(n)
        var maxFlipsCount = 0
        var permCount = 0
        var checksum = 0

        for (i in 0 until n) perm1[i] = i
        var r = n

        while (true) {

            while (r != 1) {
                count[r - 1] = r
                r--
            }

            for (i in 0 until n) perm[i] = perm1[i]
            var flipsCount = 0
            var k: Int = perm[0]

            //Perform the left rotations
            while (k != 0) {
                val k2 = k + 1 shr 1
                for (i in 0 until k2) {
                    val temp = perm[i]
                    perm[i] = perm[k - i]
                    perm[k - i] = temp
                }
                k = perm[0]
                flipsCount++
            }

            maxFlipsCount = Math.max(maxFlipsCount, flipsCount)
            checksum += if (permCount % 2 == 0) flipsCount else -flipsCount

            // Use incremental change to generate another permutation
            while (true) {
                if (r == n) {
                    println(checksum)
                    return maxFlipsCount
                }
                val perm0 = perm1[0]
                var i = 0
                while (i < r) {
                    val j = i + 1
                    perm1[i] = perm1[j]
                    i = j
                }
                perm1[r] = perm0

                count[r] = count[r] - 1
                if (count[r] > 0) break
                r++
            }

            permCount++
        }
    }

    fun runBenchmark(args: Array<String>) {
        var n = 7
        if (args.size > 0) n = Integer.parseInt(args[0])
        println("Pfannkuchen(" + n + ") = " + fannkuch(n))
    }
}