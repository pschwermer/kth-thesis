package com.benchmarks.patrik

import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * AsyncTask which takes an ArrayList of BenchmarkResult and a String representing a filename
 * and generates the benchmark result to directory "result" to the given file.
 *
 * @param Pair<ArrayList<BenchmarkResult>,String> - The benchmark results and the name of the file
 *                                                  for which to save the results to.
 * @param Void - This AsyncTask does not provide a progress status
 * @param Boolean - This AsyncTask returns true upon successfully logging the result to the file and
 *                 false otherwise.
 */
class LogResultsToFileTask : AsyncTask<Pair<ArrayList<BenchmarkResult>,String>, Void, Boolean>() {

    private val warmup: Boolean = true

    override fun doInBackground(vararg p0: Pair<ArrayList<BenchmarkResult>, String>) : Boolean{
        val (results, fileName) = p0.first()

        try {
            val root = File(Environment.getExternalStorageDirectory(), "results")
            if (!root.exists()) {
                root.mkdirs()
            }
            val resultFile = File(root,fileName)
            val writer = FileWriter(resultFile)
            appendResults(resultFile.absolutePath, results, writer)
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * Appends the benchmark results to the given [writer]. Appends both raw data as well as
     * refined metrics in applicable cases.
     */
    private fun appendResults(fileName: String, results: ArrayList<BenchmarkResult>, writer: FileWriter){
        var warmupedResults = results
        if(warmup && results.size > 10){
            warmupedResults = results.drop(10) as ArrayList<BenchmarkResult>
        }

        val resultType = warmupedResults.first()
        var rawDataFormat: String = ""
        when(resultType){
            is BoxedPrimitiveResult -> rawDataFormat = ""
            is GCResult -> rawDataFormat = "[GC Count, Time (ms), Bytes freed, Bytes Freed Per GC, Time per GC (ms)]"
            is MemoryResult -> rawDataFormat = "[Allocation Count, Allocation Size, Bytes per object]"
            is RuntimeResult -> rawDataFormat = "[Runtime (ms)]"
        }

        //Write raw data
        writer.apply {
            append("Benchmark results for $fileName \n")
            append("------------------------------------------------------------------------------\n")
            append("                  Raw data: $rawDataFormat \n")
            append("------------------------------------------------------------------------------\n")
            if(warmup && results.size > 10){
                append("Discarded results (warmup)\n")
                results.take(10).forEachIndexed{idx, result ->
                    append("($idx, ${result.resultsToString()})\n")
                }
                append("------------------------------------------------------------------------------\n")
                append("Warmuped results:\n")
            }

            warmupedResults.forEachIndexed{ idx, result ->
                append("($idx, ${result.resultsToString()})\n")
            }
            append("------------------------------------------------------------------------------\n")
        }

        appendMeanAndCI(warmupedResults, writer)
    }

    private fun appendMeanAndCI(results: ArrayList<BenchmarkResult>, writer: FileWriter){
        val resultType = results.first()
        var sampleMeans: ArrayList<Double> = arrayListOf()
        var sampleSDs: ArrayList<Double> = arrayListOf()
        var sampleCIs: ArrayList<Double> = arrayListOf()

        //Compute the sample means and standard deviation for each metric
        when(resultType){
            is BoxedPrimitiveResult -> {

            }
            is GCResult -> {
                val gcResults: Array<GCResult> = results
                        .filterIsInstance<GCResult>()
                        .toTypedArray()

                val gcCountSamples: Array<Number> = gcResults.map { sample ->
                    sample.gcCount }.toTypedArray()

                var gcTimeSamples: MutableList<Number> = gcResults.map { sample ->
                    sample.gcTime }.toMutableList()

                var gcBytesFreedSamples: MutableList<Number> = gcResults.map { sample ->
                    sample.gcBytesFreed }.toMutableList()

                var gcBytesFreedPerGCSamples: MutableList<Number> = gcResults.map { sample ->
                    sample.gcBytesFreedPerGC }.toMutableList()

                var gcTimePerGCSamples: MutableList<Number> = gcResults.map { sample ->
                    sample.gcTimePerGC }.toMutableList()
                var removed: Int = 0
                gcCountSamples.forEachIndexed { idx, sampleCount ->
                    if(sampleCount == 0) {
                        gcTimeSamples.removeAt(idx-removed)
                        gcBytesFreedSamples.removeAt(idx-removed)
                        gcBytesFreedPerGCSamples.removeAt(idx-removed)
                        gcTimePerGCSamples.removeAt(idx-removed)
                        removed++
                    }
                }

                sampleMeans.add(calculateArithmeticMean(gcCountSamples))
                sampleMeans.add(calculateArithmeticMean(gcTimeSamples.toTypedArray()))
                sampleMeans.add(calculateArithmeticMean(gcBytesFreedSamples.toTypedArray()))
                sampleMeans.add(calculateHarmonicMean(gcBytesFreedPerGCSamples.toTypedArray()))
                sampleMeans.add(calculateHarmonicMean(gcTimePerGCSamples.toTypedArray()))
                sampleSDs.add(calculateSD(gcCountSamples, sampleMeans[0]))
                sampleSDs.add(calculateSD(gcTimeSamples.toTypedArray(), sampleMeans[1]))
                sampleSDs.add(calculateSD(gcBytesFreedSamples.toTypedArray(), sampleMeans[2]))
                sampleSDs.add(calculateSD(gcBytesFreedPerGCSamples.toTypedArray(), sampleMeans[3]))
                sampleSDs.add(calculateSD(gcTimePerGCSamples.toTypedArray(), sampleMeans[4]))
            }
            is MemoryResult -> {
                val gcResults: Array<MemoryResult> = results
                        .filterIsInstance<MemoryResult>()
                        .toTypedArray()

                val memoryAllocCountSample: Array<Number> = gcResults.map { sample ->
                    sample.memAllocCount }.toTypedArray()

                val memoryAllocSizeSample: Array<Number> = gcResults.map { sample ->
                    sample.memAllocSize }.toTypedArray()

                val memoryBytesPerObjectSample: Array<Number> = gcResults.map { sample ->
                    sample.memBytesPerObject }.toTypedArray()

                sampleMeans.add(calculateArithmeticMean(memoryAllocCountSample))
                sampleMeans.add(calculateArithmeticMean(memoryAllocSizeSample))
                sampleMeans.add(calculateHarmonicMean(memoryBytesPerObjectSample))
                sampleSDs.add(calculateSD(memoryAllocCountSample, sampleMeans[0]))
                sampleSDs.add(calculateSD(memoryAllocSizeSample, sampleMeans[1]))
                sampleSDs.add(calculateSD(memoryBytesPerObjectSample, sampleMeans[2]))

            }
            is RuntimeResult -> {
                val runtimeResults: Array<RuntimeResult> = results
                        .filterIsInstance<RuntimeResult>()
                        .toTypedArray()

                val runtimeSamples: Array<Number> = runtimeResults.map { sample ->
                    sample.runtime }.toTypedArray()

                sampleMeans.add(calculateArithmeticMean(runtimeSamples))
                sampleSDs.add(calculateSD(runtimeSamples, sampleMeans[0]))
            }
        }

        //Compute the confidence interval for each result
        sampleSDs.forEach { sd ->
            sampleCIs.add(calculateConfidenceInterval(results.size, sd))
        }

        writer.append("Sample means, SD and CI (two-sided 95%)\n")

        //Append the results to the [writer]
        when(resultType){
            is BoxedPrimitiveResult -> {

            }
            is GCResult -> {
                writer.append("Count: ${sampleMeans[0]}, ${"%.4f".format(sampleSDs[0])}, ${"%.4f".format(sampleCIs[0])}\n")
                writer.append("Time: ${sampleMeans[1]}, ${"%.4f".format(sampleSDs[1])}, ${"%.4f".format(sampleCIs[1])}\n")
                writer.append("Bytes freed: ${sampleMeans[2]}, ${"%.4f".format(sampleSDs[2])}, ${"%.4f".format(sampleCIs[2])}\n")
                writer.append("Bytes freed per GC: ${"%.4f".format(sampleMeans[3])}, ${"%.4f".format(sampleSDs[3])}, ${"%.4f".format(sampleCIs[3])}\n")
                writer.append("Time per GC: ${"%.4f".format(sampleMeans[4])}, ${"%.4f".format(sampleSDs[4])}, ${"%.4f".format(sampleCIs[4])}\n")
            }
            is MemoryResult -> {
                writer.append("Allocation count: ${sampleMeans[0]}, ${"%.4f".format(sampleSDs[0])}, ${"%.4f".format(sampleCIs[0])}\n")
                writer.append("Allocation size: ${sampleMeans[1]}, ${"%.4f".format(sampleSDs[1])}, ${"%.4f".format(sampleCIs[1])}\n")
                writer.append("Bytes allocated per object: ${"%.4f".format(sampleMeans[2])}, ${"%.4f".format(sampleSDs[2])}, ${"%.4f".format(sampleCIs[2])}\n")
            }
            is RuntimeResult -> {
                val msMean = sampleMeans[0].toDouble()/1000000
                val msSD = sampleSDs[0].toDouble()/1000000
                val msCI = sampleCIs[0].toDouble()/1000000
                writer.append("Runtime: ${"%.4f".format(msMean)}, ${"%.4f".format(msSD)}, ${"%.4f".format(msCI)}\n")
            }
        }

    }

    private fun calculateArithmeticMean(sample: Array<Number>): Double{
        if (sample.isEmpty()) return 0.0
        val sum = sample.fold(0.0, {acc, elem -> acc + elem.toDouble()})
        val mean = sum/sample.size
        return mean
    }

    private fun calculateHarmonicMean(sample: Array<Number>): Double{
        if (sample.isEmpty()) return 0.0
        val sum = sample.fold(0.0, {acc, elem -> acc + 1/elem.toDouble()})
        val mean = sample.size/sum
        return mean
    }

    private fun calculateSD(sample: Array<Number>, sampleMean: Double): Double{
        if (sample.isEmpty()) return 0.0
        val sd = sample.fold(0.0, { acc, elem -> acc + Math.pow(elem.toDouble() - sampleMean, 2.0) })
        return Math.sqrt(sd / (sample.size-1))
    }

    private fun calculateConfidenceInterval(sampleSize: Int, sd: Double): Double{
        if (sampleSize == 0) return 0.0
        val ci = 1.96*(sd / Math.sqrt(sampleSize.toDouble()))
        return ci
    }
}