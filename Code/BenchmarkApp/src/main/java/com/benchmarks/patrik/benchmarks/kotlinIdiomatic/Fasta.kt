package com.benchmarks.patrik.benchmarks.kotlinIdiomatic

import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

/* The Computer Language Benchmarks Game
   http://benchmarksgame.alioth.debian.org/

   implemented in Kotlin by Patrik Schwermer
*/

data class AminoAcid(var char: Char, var prob: Double)

class Fasta {

    private val LINE_LENGTH = 60
    private val LINES = 99
    val IM = 139968
    val IA = 3877
    val IC = 29573
    var seed = 42.0

    /*
       Generates a DNA sequence of length [chars] by copying from the ALU sequence.
     */
    private fun generateDNASequenceByRepeatingSelection(id: String, desc: String, length: Int, dnaSequence: CharArray, writer: BufferedWriter) {
        writer.append(">$id $desc \n")

        var seqIndex = 0
        var lineCount = 0
        var chars = length
        while (chars > 0) {

            val chunkSize: Int = when (chars >= LINE_LENGTH) {
                true -> LINE_LENGTH
                false -> chars
            }

            if (lineCount >= LINES) {
                writer.flush()
                lineCount = 0
            }

            for (i in 0 until chunkSize) {
                if (seqIndex == dnaSequence.size) seqIndex = 0
                writer.append(dnaSequence[seqIndex++])
            }

            lineCount++
            writer.append('\n')
            chars -= chunkSize
        }

        writer.flush()
    }

    /*
        Generates a DNA-sequence by weighted pseudo-random selection
     */
    private fun generateDNASequenceByRandomSelection(id: String, desc: String, length: Int, acids: Array<AminoAcid>, writer: BufferedWriter) {
        writer.append(">$id $desc \n")

        var lineCount = 0
        var chars = length
        while (chars > 0) {

            val chunkSize: Int = when (chars >= LINE_LENGTH) {
                true -> LINE_LENGTH
                false -> chars
            }

            if (lineCount >= LINES) {
                writer.flush()
                lineCount = 0
            }

            //Generate [chunkSize] elements into buffer by pseudo-random selection using linear search
            for (i in 0 until chunkSize) {
                seed = (seed * IA + IC) % IM
                val random: Double = seed / IM

                for (j in 0 until acids.size) {
                    if (acids[j].prob >= random) {
                        writer.append(acids[j].char)
                        break
                    }
                }
            }

            writer.append('\n')
            lineCount++
            chars -= chunkSize
        }

        writer.flush()
    }

    private fun accumulate(acids: Array<AminoAcid>): Array<AminoAcid> {
        for (i in 1 until acids.size) {
            acids[i].prob += acids[i - 1].prob
        }

        return acids
    }

    fun runBenchmark(args: Array<String>) {
        val n: Int = args[0].toInt()

        //Alphabet and sequence data
        val alu = ("GGCCGGGCGCGGTGGCTCACGCCTGTAATCCCAGCACTTTGG"
                + "GAGGCCGAGGCGGGCGGATCACCTGAGGTCAGGAGTTCGAGA"
                + "CCAGCCTGGCCAACATGGTGAAACCCCGTCTCTACTAAAAAT"
                + "ACAAAAATTAGCCGGGCGTGGTGGCGCGCGCCTGTAATCCCA"
                + "GCTACTCGGGAGGCTGAGGCAGGAGAATCGCTTGAACCCGGG"
                + "AGGCGGAGGTTGCAGTGAGCCGAGATCGCGCCACTGCACTCC"
                + "AGCCTGGGCGACAGAGCGAGACTCCGTCTCAAAAA").toCharArray()

        val iubChars = arrayOf('a', 'c', 'g', 't', 'B', 'D', 'H', 'K', 'M', 'N', 'R', 'S', 'V', 'W', 'Y')
        val iubProbs = arrayOf(0.27, 0.12, 0.12, 0.27, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02)
        var iubAcids: Array<AminoAcid> = iubChars.mapIndexed { idx, char ->
            AminoAcid(char, iubProbs[idx])
        }.toTypedArray()

        val hsChars = listOf('a', 'c', 'g', 't')
        val hsProbs = listOf(0.3029549426680, 0.1979883004921, 0.1975473066391, 0.3015094502008)
        var homosapiensAcids: Array<AminoAcid> = hsChars.mapIndexed { idx, char ->
            AminoAcid(char, hsProbs[idx])
        }.toTypedArray()

        //Create fasta instance and accumulate probabilities
        val fasta = Fasta()
        iubAcids = fasta.accumulate(iubAcids)
        homosapiensAcids = fasta.accumulate(homosapiensAcids)

        //Compute results
        val writer = BufferedWriter(OutputStreamWriter(System.out))
        fasta.generateDNASequenceByRepeatingSelection("ONE", "Homo sapiens alu", 2 * n, alu, writer)
        fasta.generateDNASequenceByRandomSelection("TWO", "IUB ambiguity codes", 3 * n, iubAcids, writer)
        fasta.generateDNASequenceByRandomSelection("THREE", "Homo sapiens frequency", 5 * n, homosapiensAcids, writer)

        writer.flush()
        writer.close()

    }
}