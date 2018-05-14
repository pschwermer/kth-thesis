package com.benchmarks.patrik

abstract class BenchmarkResult {
    abstract fun resultsToString(): String
}

class RuntimeResult(val runtime: Long) : BenchmarkResult(){
    override fun resultsToString(): String {
        val ms = runtime.toDouble()/1000000
        val resultString = "%.4f".format(ms)
        return resultString
    }
}

class BoxedPrimitiveResult : BenchmarkResult(){
    override fun resultsToString(): String {
        TODO("not implemented")
    }
}

class GCResult(val gcCount: Int, val gcTime: Int, val gcBytesFreed: Long, val gcBytesFreedPerGC: Double, val gcTimePerGC: Double) : BenchmarkResult(){
    override fun resultsToString(): String {
        val resultString = "$gcCount, $gcTime, $gcBytesFreed, $gcBytesFreedPerGC, $gcTimePerGC"
        return resultString
    }
}

class MemoryResult(val memAllocCount: Int, val memAllocSize: Int, val memBytesPerObject: Double) : BenchmarkResult(){
    override fun resultsToString(): String {
        val resultString = "$memAllocCount, $memAllocSize, $memBytesPerObject"
        return resultString
    }
}