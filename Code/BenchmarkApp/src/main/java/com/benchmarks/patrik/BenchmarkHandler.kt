package com.benchmarks.patrik

import android.icu.text.SimpleDateFormat
import android.os.*
import android.util.Log
import java.io.File
import java.util.*
import com.benchmarks.patrik.benchmarks.java.nbody as NBodyJava
import com.benchmarks.patrik.benchmarks.kotlinIdiomatic.nbody as NBodyKotlinIdiomatic
import com.benchmarks.patrik.benchmarks.kotlinConverted.nbody as NBodyKotlinConverted
import com.benchmarks.patrik.benchmarks.java.FannkuchRedux as FannkuchReduxJava
import com.benchmarks.patrik.benchmarks.kotlinIdiomatic.FannkuchRedux as FannkuchReduxKotlinIdiomatic
import com.benchmarks.patrik.benchmarks.kotlinConverted.FannkuchRedux as FannkuchReduxKotlinConverted
import com.benchmarks.patrik.benchmarks.java.Fasta as FastaJava
import com.benchmarks.patrik.benchmarks.kotlinIdiomatic.Fasta as FastaKotlinIdiomatic
import com.benchmarks.patrik.benchmarks.kotlinConverted.Fasta as FastaKotlinConverted
import com.benchmarks.patrik.benchmarks.java.ReverseComplement as RevCompJava
import com.benchmarks.patrik.benchmarks.kotlinIdiomatic.ReverseComplement as RevCompKotlinIdiomatic
import com.benchmarks.patrik.benchmarks.kotlinConverted.ReverseComplement as RevCompKotlinConverted

/**
 * Handler for executing benchmarks. Accepts messages of type BenchmarkHandlerMessage and
 * executes a benchmark upon receiving a message of status type BenchmarkMessage.START_BENCHMARK.
 * Communicates back to [uiHandler] upon successfully having executed a benchmark with the benchmark
 * results.
 *
 * @property looper - The looper for the benchmark handler thread
 * @property uiHandler - The handler for which to communicate back upon completing a benchmark
 */

class BenchmarkHandler(looper: Looper, private val uiHandler: Handler) : Handler(looper) {

    private val nBodyJava = NBodyJava()
    private val nBodyKotlinConverted = NBodyKotlinConverted()
    private val nBodyKotlinIdiomatic = NBodyKotlinIdiomatic()
    private val nBodyArgs = arrayOf("350000")

    private val fannkuchReduxJava = FannkuchReduxJava()
    private val fannkuchReduxKotlinConverted = FannkuchReduxKotlinConverted()
    private val fannkuchReduxKotlinIdiomatic = FannkuchReduxKotlinIdiomatic()
    private val fannkuchReduxArgs = arrayOf("9")

    private val fastaJava = FastaJava()
    private val fastaKotlinConverted = FastaKotlinConverted()
    private val fastaKotlinIdiomatic = FastaKotlinIdiomatic()
    private val fastaArgs = arrayOf("350000")

    private val revCompJava = RevCompJava()
    private val revCompKotlinIdiomatic = RevCompKotlinIdiomatic()
    private val revCompKotlinConverted = RevCompKotlinConverted()
    private val revCompArgs = arrayOf("fasta225000.txt")

    /**
     * Handles messages of type BenchmarkStatus. Supports:
     * - BenchmarkStatus.START_BENCHMARK
     */
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            BenchmarkStatus.START_BENCHMARK.id -> {
                val (type, implementation, metric, data) = msg.obj as BenchmarkMessage
                startMeasuringMetric(type, implementation, metric, data)
            }
        }
    }

    /**
     * Starts measuring the [benchmarkMetric] for the given [benchmarkType] and
     * [benchmarkImplementation]. Sends a message of type BenchmarkStatus.FINISHED to the
     * UI thread upon successfully finishing benchmarking containing the benchmark results.
     */
    private fun startMeasuringMetric(benchmarkType: BenchmarkType,
                                     benchmarkImplementation: BenchmarkImplementation,
                                     benchmarkMetric: BenchmarkMetric,
                                     data: Any?) {

        var benchmarkResult: BenchmarkResult? = null

        when (benchmarkMetric) {
            BenchmarkMetric.BOXING_OF_PRIMITIVES -> {
                if(data != null){
                    val fileName: String = data as String
                    val dateAndTime = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())
                    val root = File(Environment.getExternalStorageDirectory(), "bopResults")
                    if (!root.exists()) {
                        root.mkdirs()
                    }

                    Debug.startMethodTracing("${Environment.getExternalStorageDirectory().path}/bopResults/$fileName$dateAndTime.trace")
                    executeBenchmark(benchmarkType, benchmarkImplementation)
                    Debug.stopMethodTracing()
                } else {
                    executeBenchmark(benchmarkType, benchmarkImplementation)
                }

                benchmarkResult = BoxedPrimitiveResult()
            }
            BenchmarkMetric.GARBAGE_COLLECTION -> {
                val gcCountStart: Int = Debug.getRuntimeStat("art.gc.gc-count").toInt()
                val gcTimeStart: Int = Debug.getRuntimeStat("art.gc.gc-time").toInt()
                //val gcBytesAllocStart: Long = Debug.getRuntimeStat("art.gc.bytes-allocated").toLong()
                val gcBytesFreedStart: Long = Debug.getRuntimeStat("art.gc.bytes-freed").toLong()

                executeBenchmark(benchmarkType, benchmarkImplementation)

                val gcCountEnd: Int = Debug.getRuntimeStat("art.gc.gc-count").toInt()
                val gcTimeEnd: Int = Debug.getRuntimeStat("art.gc.gc-time").toInt()
                //val gcBytesAllocEnd: Long = Debug.getRuntimeStat("art.gc.bytes-allocated").toLong()
                val gcBytesFreedEnd: Long = Debug.getRuntimeStat("art.gc.bytes-freed").toLong()

                val gcCount = gcCountEnd-gcCountStart
                val gcTime = gcTimeEnd-gcTimeStart
                val gcBytesFreed = gcBytesFreedEnd-gcBytesFreedStart
                var gcBytesFreedPerGC = 0.0
                if(gcCount != 0) gcBytesFreedPerGC = gcBytesFreed.toDouble()/gcCount
                var gcTimePerGC = 0.0
                if(gcCount != 0) gcTimePerGC = gcTime.toDouble()/gcCount

                benchmarkResult = GCResult(gcCount,
                        gcTime,
                        gcBytesFreed,
                        gcBytesFreedPerGC,
                        gcTimePerGC)
            }
            BenchmarkMetric.MEMORY_CONSUMPTION -> {
                Debug.resetThreadAllocCount()
                Debug.resetThreadAllocSize()
                Debug.startAllocCounting()

                executeBenchmark(benchmarkType, benchmarkImplementation)

                Debug.stopAllocCounting()

                val memoryAllocCount = Debug.getThreadAllocCount()
                val memoryAllocSize = Debug.getThreadAllocSize()
                val memoryBytesPerObject = memoryAllocSize.toDouble()/memoryAllocCount

                benchmarkResult = MemoryResult(memoryAllocCount, memoryAllocSize, memoryBytesPerObject)
            }
            BenchmarkMetric.RUNTIME -> {
                val startTime = SystemClock.elapsedRealtimeNanos()
                executeBenchmark(benchmarkType, benchmarkImplementation)
                val endTime = SystemClock.elapsedRealtimeNanos()
                benchmarkResult = RuntimeResult(endTime-startTime)
            }
        }

        sendMessageToUiThread(BenchmarkStatus.BENCHMARK_FINISHED, benchmarkResult)
    }

    /**
     * Executes the given [benchmarkType] and [benchmarkImplementation] by providing the benchmark
     * implementation with the benchmark type arguments (e.g. nBodyArgs in case of nBody).
     */
    private fun executeBenchmark(benchmarkType: BenchmarkType, benchmarkImplementation: BenchmarkImplementation) {
        when (benchmarkType) {
            BenchmarkType.FASTA -> {
                when (benchmarkImplementation) {
                    BenchmarkImplementation.JAVA -> fastaJava.runBenchmark(fastaArgs)
                    BenchmarkImplementation.KOTLIN_IDIOMATIC -> fastaKotlinIdiomatic.runBenchmark(fastaArgs)
                    BenchmarkImplementation.KOTLIN_CONVERTED -> fastaKotlinConverted.runBenchmark(fastaArgs)
                }
            }
            BenchmarkType.FANNKUCH_REDUX -> {
                when (benchmarkImplementation) {
                    BenchmarkImplementation.JAVA -> fannkuchReduxJava.runBenchmark(fannkuchReduxArgs)
                    BenchmarkImplementation.KOTLIN_IDIOMATIC -> fannkuchReduxKotlinIdiomatic.runBenchmark(fannkuchReduxArgs)
                    BenchmarkImplementation.KOTLIN_CONVERTED -> fannkuchReduxKotlinConverted.runBenchmark(fannkuchReduxArgs)
                }
            }
            BenchmarkType.N_BODY -> {
                when (benchmarkImplementation) {
                    BenchmarkImplementation.JAVA -> nBodyJava.runBenchmark(nBodyArgs)
                    BenchmarkImplementation.KOTLIN_IDIOMATIC -> nBodyKotlinIdiomatic.runBenchmark(nBodyArgs)
                    BenchmarkImplementation.KOTLIN_CONVERTED -> nBodyKotlinConverted.runBenchmark(nBodyArgs)
                }
            }
            BenchmarkType.REVERSE_COMPLEMENT -> {
                when(benchmarkImplementation){
                    BenchmarkImplementation.JAVA -> revCompJava.runBenchmark(revCompArgs)
                    BenchmarkImplementation.KOTLIN_IDIOMATIC -> revCompKotlinIdiomatic.runBenchmark(revCompArgs)
                    BenchmarkImplementation.KOTLIN_CONVERTED -> revCompKotlinConverted.runBenchmark(revCompArgs)
                }
            }
        }
    }

    /**
     * Sends a message to the UI thread of type [status] containing [objectToSend]
     */
    private fun sendMessageToUiThread(status: BenchmarkStatus, objectToSend: Any?) {
        uiHandler.obtainMessage().apply {
            what = status.id
            obj = objectToSend
        }.sendToTarget()
    }
}