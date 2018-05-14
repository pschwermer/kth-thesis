package com.benchmarks.patrik.benchmarks.kotlinConverted

/*
 * The Computer Language Benchmarks Game
 * http://benchmarksgame.alioth.debian.org/
 *
 * modified by Mehmet D. AKIN
 * modified by Rikard Mustajärvi
 * mostly auto-converted to Kotlin by Patrik Schwermer
 */

import java.io.IOException
import java.io.OutputStream

class Fasta {
    val LINE_LENGTH = 60
    val BUFFER_SIZE = (LINE_LENGTH + 1) * 1024 // add 1 for '\n'

    // Weighted selection from alphabet
    var ALU = (
            "GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG"
                    + "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA"
                    + "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT"
                    + "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA"
                    + "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG"
                    + "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC"
                    + "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA")

    private val IUB = FloatProbFreq(
            byteArrayOf('a'.toByte(), 'c'.toByte(), 'g'.toByte(), 't'.toByte(), 'B'.toByte(), 'D'.toByte(), 'H'.toByte(), 'K'.toByte(), 'M'.toByte(), 'N'.toByte(), 'R'.toByte(), 'S'.toByte(), 'V'.toByte(), 'W'.toByte(), 'Y'.toByte()),
            doubleArrayOf(0.27, 0.12, 0.12, 0.27, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02)
    )

    private val HOMO_SAPIENS = FloatProbFreq(
            byteArrayOf('a'.toByte(), 'c'.toByte(), 'g'.toByte(), 't'.toByte()),
            doubleArrayOf(0.3029549426680, 0.1979883004921, 0.1975473066391, 0.3015094502008)
    )

    @Throws(IOException::class)
    fun makeRandomFasta(id: String, desc: String,
                        fpf: FloatProbFreq, nChars: Int, writer: OutputStream) {
        var nChars = nChars
        val buffer = ByteArray(BUFFER_SIZE)

        if (buffer.size % (LINE_LENGTH + 1) != 0) {
            throw IllegalStateException(
                    "buffer size must be a multiple of " + "line length (including line break)")
        }

        val descStr = ">" + id + " " + desc + '\n'.toString()
        writer.write(descStr.toByteArray())

        var bufferIndex = 0
        while (nChars > 0) {
            val chunkSize: Int
            if (nChars >= LINE_LENGTH) {
                chunkSize = LINE_LENGTH
            } else {
                chunkSize = nChars
            }

            if (bufferIndex == BUFFER_SIZE) {
                writer.write(buffer, 0, bufferIndex)
                bufferIndex = 0
            }

            bufferIndex = fpf.selectRandomIntoBuffer(buffer, bufferIndex, chunkSize)
            buffer[bufferIndex++] = '\n'.toByte()

            nChars -= chunkSize
        }

        writer.write(buffer, 0, bufferIndex)
    }

    @Throws(IOException::class)
    fun makeRepeatFasta(
            id: String, desc: String, alu: String,
            nChars: Int, writer: OutputStream) {
        var nChars = nChars
        val aluBytes = alu.toByteArray()
        var aluIndex = 0

        val buffer = ByteArray(BUFFER_SIZE)

        if (buffer.size % (LINE_LENGTH + 1) != 0) {
            throw IllegalStateException(
                    "buffer size must be a multiple " + "of line length (including line break)")
        }

        val descStr = ">" + id + " " + desc + '\n'.toString()
        writer.write(descStr.toByteArray())

        var bufferIndex = 0
        while (nChars > 0) {
            val chunkSize: Int
            if (nChars >= LINE_LENGTH) {
                chunkSize = LINE_LENGTH
            } else {
                chunkSize = nChars
            }

            if (bufferIndex == BUFFER_SIZE) {
                writer.write(buffer, 0, bufferIndex)
                bufferIndex = 0
            }

            for (i in 0 until chunkSize) {
                if (aluIndex == aluBytes.size) {
                    aluIndex = 0
                }

                buffer[bufferIndex++] = aluBytes[aluIndex++]
            }
            buffer[bufferIndex++] = '\n'.toByte()

            nChars -= chunkSize
        }

        writer.write(buffer, 0, bufferIndex)
    }

    @Throws(IOException::class)
    fun runBenchmark(args: Array<String>) {
        var n = 1000
        //        int n = 25000000;
        if (args.size > 0) {
            n = Integer.parseInt(args[0])
        }

        val out = System.out
        makeRepeatFasta("ONE", "Homo sapiens alu", ALU, n * 2, out)
        makeRandomFasta("TWO", "IUB ambiguity codes", IUB, n * 3, out)
        makeRandomFasta("THREE", "Homo sapiens frequency", HOMO_SAPIENS, n * 5, out)
        out.close()
    }

    class FloatProbFreq(internal val chars: ByteArray, probs: DoubleArray) {
        internal val probs: FloatArray

        init {
            this.probs = FloatArray(probs.size)
            for (i in probs.indices) {
                this.probs[i] = probs[i].toFloat()
            }
            makeCumulative()
        }

        private fun makeCumulative() {
            var cp = 0.0
            for (i in probs.indices) {
                cp += probs[i].toDouble()
                probs[i] = cp.toFloat()
            }
        }

        fun selectRandomIntoBuffer(buffer: ByteArray, bufferIndex: Int, nRandom: Int): Int {
            var bufferIndex = bufferIndex
            val chars = this.chars
            val probs = this.probs
            val len = probs.size

            outer@ for (rIndex in 0 until nRandom) {
                val r = random(1.0f)
                for (i in 0 until len) {
                    if (r < probs[i]) {
                        buffer[bufferIndex++] = chars[i]
                        continue@outer
                    }
                }

                buffer[bufferIndex++] = chars[len - 1]
            }

            return bufferIndex
        }

        companion object {
            internal var last = 42
            val IM = 139968
            val IA = 3877
            val IC = 29573

            // pseudo-random number generator
            fun random(max: Float): Float {
                val oneOverIM = 1.0f / IM
                last = (last * IA + IC) % IM
                return max * last.toFloat() * oneOverIM
            }
        }
    }
}