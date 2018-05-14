import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStream

class DEXParser {

    /**
     * Parses the given DEX file and extracts the Dalvik Bytecode Instruction sequence
     */
    fun parseDEXFile(file: File): List<Instruction> {
        val inputStream: InputStream = file.inputStream()
        val instMap = getInstructionMap()
        val instSeq: MutableList<Instruction> = mutableListOf()

        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                val bytecodeToken = it.split(" ")
                bytecodeToken.forEach {
                    if (it == ".method" || it == ".end") {
                        instSeq.add(Instruction.NO_INST)
                    } else {
                        val instToken: Instruction? = instMap[it]
                        if (instToken != null) instSeq.add(instToken)
                    }
                }
            }
        }

        return instSeq.toList()
    }

    /**
     * Computes the consecutive instruction sequences of size n.
     */
    fun computeNgramFrequency(size: Int, instSeq: List<Instruction>): Map<List<Instruction>, Double> {
        val ngrams: MutableMap<List<Instruction>, Double> = mutableMapOf()
        var totalInst = 0.0

        for (inst in instSeq.indices) {
            if ((instSeq.size - size) >= inst) {
                val ngramSeq = instSeq.subList(inst, inst + size)
                if (!ngramSeq.contains(Instruction.NO_INST)) {
                    val count = ngrams[ngramSeq]
                    if (count != null) {
                        ngrams[ngramSeq] = count + 1.0
                    } else {
                        ngrams[ngramSeq] = 1.0
                    }
                    totalInst++
                }
            }
        }

        return ngrams.mapValues { inst -> inst.value/totalInst}.toMap()
    }

    /**
     * Returns a pair of the unique ngram sequences of size 'size' and the total ngram sequences.
     */
    fun computeNgramStatistics(size: Int, instSeq: List<Instruction>): Pair<Int, Int> {
        val ngrams: MutableMap<List<Instruction>, Boolean> = mutableMapOf()
        var totalInst = 0

        for (inst in instSeq.indices) {
            if ((instSeq.size - size) >= inst) {
                val ngramSeq = instSeq.subList(inst, inst + size)
                if (!ngramSeq.contains(Instruction.NO_INST)) {
                    ngrams[ngramSeq] = true
                    totalInst++
                }
            }
        }

        return Pair(ngrams.size, totalInst)
    }
}

fun main(args: Array<String>) {
    val parser = DEXParser()
    var files = mutableListOf<Pair<String,File>>()
    var instSequences = mutableListOf<List<Instruction>>()
    val ngramSize = args.first().toInt()
    var allNgrams = getInstructionNgrams(ngramSize)
    var ngramFrequencyMaps = mutableListOf<Map<List<Instruction>, Double>>()
    var ngramCounts = mutableListOf<Pair<Int,Int>>()

    //Append files to 'files' list
    for(i in 1 until args.size){
        println(args[i])
        files.add(Pair(args[i], File(args[i])))
    }

    //Extract bytecode instruction sequences from all files
    files.forEach{(_, file) ->
        instSequences.add((parser.parseDEXFile(file)))
    }

    //Compute n-gram frequencies for all files
    for(i in 0 until files.size){
        ngramFrequencyMaps.add(parser.computeNgramFrequency(ngramSize, instSequences[i]))
    }

    //Compute n-gram counts
    for(i in 0 until files.size){
        ngramCounts.add(parser.computeNgramStatistics(ngramSize,instSequences[i]))
    }

    val frequencyResults = BufferedWriter(FileWriter(File("ngram-frequency-results.csv")))
    val countResults = BufferedWriter(FileWriter(File("ngram-count-results.txt")))

    //Write ngram frequency results
    frequencyResults.write("\"\",\"\",\"\"")
    files.forEach{(filename, _) ->
        frequencyResults.write(", \"$filename\"")
    }
    frequencyResults.write("\n")

    frequencyResults.write("Implementation, \"\", \"\"")
    frequencyResults.write(", \"java\", \"java\", \"java\", \"java\"")
    frequencyResults.write(", \"kotlin-converted\", \"kotlin-converted\", \"kotlin-converted\", \"kotlin-converted\"")
    frequencyResults.write(", \"kotlin-idiomatic\", \"kotlin-idiomatic\", \"kotlin-idiomatic\", \"kotlin-idiomatic\"")
    frequencyResults.write("\n")

    allNgrams.forEach { instSeq ->
        if(ngramFrequencyMaps.any { it.contains(instSeq) }){
            frequencyResults.write("\"${instSeq.joinToString(separator = " ")}\"")
            frequencyResults.write("\"\",\"\",\"\"")
            ngramFrequencyMaps.forEach { map ->
                if(map.contains(instSeq)){
                    frequencyResults.write(", ${map[instSeq]}")
                } else {
                    frequencyResults.write(", 0.0")
                }
            }
            frequencyResults.write("\n")
        }
    }

    frequencyResults.flush()
    frequencyResults.close()

    //Write count results
    var totalUniqueSeq = mutableListOf<Instruction>()
    var fannkuchUniqueSeq = mutableListOf<Instruction>()
    var fastaUniqueSeq = mutableListOf<Instruction>()
    var nbodyUniqueSeq = mutableListOf<Instruction>()
    var reverseUniqueSeq = mutableListOf<Instruction>()

    for(i in 0 until instSequences.size){
        totalUniqueSeq.addAll(instSequences[i])

        if(i == 0 || i == 4 || i == 8){
            fannkuchUniqueSeq.addAll(instSequences[i])
            fannkuchUniqueSeq.add(Instruction.NO_INST)
        } else if(i == 1 || i == 5 || i == 9){
            reverseUniqueSeq.addAll(instSequences[i])
            reverseUniqueSeq.add(Instruction.NO_INST)
        } else if(i == 2 || i == 6 || i == 10){
            fastaUniqueSeq.addAll(instSequences[i])
            fastaUniqueSeq.add(Instruction.NO_INST)
        } else if(i == 3 || i == 7 || i == 11){
            nbodyUniqueSeq.addAll(instSequences[i])
            nbodyUniqueSeq.add(Instruction.NO_INST)
        }
    }

    var totalUnique = parser.computeNgramStatistics(ngramSize, totalUniqueSeq).first
    var fannkuchUnique = parser.computeNgramStatistics(ngramSize, fannkuchUniqueSeq).first
    var fastaUnique = parser.computeNgramStatistics(ngramSize, fastaUniqueSeq).first
    var nbodyUnique = parser.computeNgramStatistics(ngramSize, nbodyUniqueSeq).first
    var reverseUnique = parser.computeNgramStatistics(ngramSize, reverseUniqueSeq).first

    var fannkuchString = ""
    var fastaString = ""
    var nBodyString = ""
    var reverseString = ""

    countResults.write("Benchmark, Unique $ngramSize-gram (per implementation, total for benchmark, total for all), Total $ngramSize-gram \n")
    for(i in 0 until ngramCounts.size){
        val fileName = files[i].first
        val unique = ngramCounts[i].first
        val total = ngramCounts[i].second

        if(i == 0 || i == 4 || i == 8){
            fannkuchString += "$fileName, $unique/${(unique.toDouble()/fannkuchUnique)*100}/${(unique.toDouble()/totalUnique)*100}, $total \n"
        } else if(i == 1 || i == 5 || i == 9){
            reverseString += "$fileName, $unique/${(unique.toDouble()/reverseUnique)*100}/${(unique.toDouble()/totalUnique)*100}, $total \n"
        } else if(i == 2 || i == 6 || i == 10){
            fastaString += "$fileName, $unique/${(unique.toDouble()/fastaUnique)*100}/${(unique.toDouble()/totalUnique)*100}, $total \n"
        } else if(i == 3 || i == 7 || i == 11){
            nBodyString += "$fileName, $unique/${(unique.toDouble()/nbodyUnique)*100}/${(unique.toDouble()/totalUnique)*100}, $total \n"
        }
    }

    countResults.write(fastaString)
    countResults.write(fannkuchString)
    countResults.write(nBodyString)
    countResults.write(reverseString)

    countResults.flush()
    countResults.close()
}