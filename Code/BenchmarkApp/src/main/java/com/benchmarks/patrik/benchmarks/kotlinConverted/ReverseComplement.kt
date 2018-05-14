package com.benchmarks.patrik.benchmarks.kotlinConverted

import java.io.InputStream

/*
 * The Computer Language Benchmarks Game
 * http://benchmarksgame.alioth.debian.org/
 * contributed by Tao Lei
 * mostly auto-converted to Kotlin by Patrik Schwermer
 */

class ReverseComplement {

    @Throws(Exception::class)
    fun runBenchmark(args: Array<String>) {
        //Sends file instead of reading from System.in!
        val `is` = this.javaClass.classLoader.getResourceAsStream("assets/" + args[0])
        solve(`is`)
    }

    companion object {

        private val transFrom = "ACGTUMRWSYKVHDBN"
        private val transTo = "TGCAAKYWSRMBDHVN"
        private val transMap = ByteArray(128)

        init {
            for (i in transMap.indices) transMap[i] = i.toByte()
            for (i in 0 until transFrom.length) {
                val c = transFrom[i]
                transMap[Character.toLowerCase(c).toInt()] = transTo[i].toByte()
                transMap[c.toInt()] = transMap[Character.toLowerCase(c).toInt()]
            }
        }

        private var buffer = ByteArray(65536)
        private var `in` = System.`in`
        private var pos: Int = 0
        private var limit: Int = 0
        private var start: Int = 0
        private var end: Int = 0

        private fun endPos(): Int {
            for (off in pos until limit) {
                if (buffer[off] == '\n'.toByte())
                    return off
            }
            return -1
        }

        @Throws(Exception::class)
        private fun nextLine(): Boolean {
            while (true) {
                end = endPos()
                if (end >= 0) {
                    start = pos
                    pos = end + 1
                    if (buffer[end - 1] == '\r'.toByte())
                        end--
                    while (buffer[start] == ' '.toByte()) start++
                    while (end > start && buffer[end - 1] == ' '.toByte()) end--
                    if (end > start)
                        return true
                } else {
                    if (pos > 0 && limit > pos) {
                        limit -= pos
                        System.arraycopy(buffer, pos, buffer, 0, limit)
                        pos = 0
                    } else {
                        limit = 0
                        pos = limit
                    }
                    val r = `in`.read(buffer, limit, buffer.size - limit)
                    if (r < 0)
                        return false
                    limit += r
                }
            }
        }

        private var LINE_WIDTH = 0
        private var data = ByteArray(1 shl 20)
        private var size: Int = 0
        private var outputBuffer = ByteArray(65536)
        private var outputPos = 0

        @Throws(Exception::class)
        private fun flushData() {
            System.out.write(outputBuffer, 0, outputPos)
            outputPos = 0
        }

        @Throws(Exception::class)
        private fun prepareWrite(len: Int) {
            if (outputPos + len > outputBuffer.size)
                flushData()
        }

        @Throws(Exception::class)
        private fun write(b: Int) {
            outputBuffer[outputPos++] = b.toByte()
        }

        @Throws(Exception::class)
        private fun write(b: ByteArray, off: Int, len: Int) {
            prepareWrite(len)
            System.arraycopy(b, off, outputBuffer, outputPos, len)
            outputPos += len
        }

        @Throws(Exception::class)
        private fun finishData() {
            while (size > 0) {
                var len = Math.min(LINE_WIDTH, size)
                prepareWrite(len + 1)
                while (len-- != 0) {
                    write(data[--size].toInt())
                }
                write('\n'.toInt())
            }
            resetData()
        }

        private fun resetData() {
            LINE_WIDTH = 0
            size = 0
        }

        @Throws(Exception::class)
        private fun appendLine() {
            val len = end - start
            if (LINE_WIDTH == 0) LINE_WIDTH = len
            if (size + len > data.size) {
                val data0 = data
                data = ByteArray(data.size * 2)
                System.arraycopy(data0, 0, data, 0, size)
            }
            for (i in start until end) {
                data[size++] = transMap[buffer[i].toInt()]
            }
        }

        @Throws(Exception::class)
        private fun solve(ins: InputStream) {
            `in` = ins
            limit = 0
            pos = limit
            outputPos = 0
            resetData()
            while (nextLine()) {
                if (buffer[start] == '>'.toByte()) {
                    finishData()
                    write(buffer, start, pos - start)
                } else {
                    appendLine()
                }
            }
            finishData()
            if (outputPos > 0) flushData()
            System.out.flush()
        }
    }
}