package com.benchmarks.patrik.benchmarks.kotlinIdiomatic

import java.io.BufferedWriter
import java.io.OutputStreamWriter

/*
 * The Computer Language Benchmarks Game
 * http://benchmarksgame.alioth.debian.org/
 * implemented in Kotlin by Patrik Schwermer
 */

class ReverseComplement {
    private var ins = System.`in`
    private var sequence: MutableList<Char> = mutableListOf()
    private val transMap: HashMap<Char, Char> = hashMapOf()

    init{
        val nucleotides = "AaCcGgTtUuMmRrWwSsYyKkVvHhDdBbNn"
        val complements = "TTGGCCAAAAKKYYWWSSRRMMBBDDHHVVNN"

        for(i in 0 until nucleotides.length){
            transMap[nucleotides[i]] = complements[i]
        }
    }

    private fun solve(){
        read()
    }

    private fun read(){
        val reader = ins.bufferedReader()

        var prevSeqName = ""
        reader.useLines { lines ->
            lines.forEach { line ->
                if(line[0] == '>'){
                    if(prevSeqName != ""){
                        write(prevSeqName)
                    }
                    prevSeqName = line
                } else {
                    line.forEach { char ->
                        val complement: Char? = transMap[char]
                        if(complement != null) sequence.add(complement)
                    }
                }
            }
        }

        write(prevSeqName)
        reader.close()
    }

    private fun write(sequenceName: String){
        val writer = BufferedWriter(OutputStreamWriter(System.out))
        writer.append("$sequenceName\n")

        var counter = 0
        for(i in sequence.size-1 downTo 0){
                counter++
                writer.append(sequence[i])
                if(counter%60==0 && counter != 1){
                    writer.append('\n')
                    //Flush every 100 lines to avoid overfull buffer causing untimely print
                    if(counter%100 == 0) writer.flush()
                }
        }
        writer.append('\n')

        sequence = mutableListOf()
        writer.close()
    }

    fun runBenchmark(args: Array<String>){
        ins = this.javaClass.classLoader.getResourceAsStream("assets/" + args[0])
        solve()
    }
}