package com.benchmarks.patrik

enum class BenchmarkType {
    FASTA, FANNKUCH_REDUX, N_BODY, REVERSE_COMPLEMENT
}

enum class BenchmarkImplementation {
    JAVA, KOTLIN_CONVERTED, KOTLIN_IDIOMATIC
}

enum class BenchmarkMetric{
    BOXING_OF_PRIMITIVES, GARBAGE_COLLECTION, MEMORY_CONSUMPTION, RUNTIME
}

enum class BenchmarkStatus(val id: Int){
    START_BENCHMARK(1),
    BENCHMARK_FINISHED(0),
    UPDATE_PROGRESS(2),
    UPDATE_MESSAGE(3)
}

data class BenchmarkMessage(val type: BenchmarkType, val implementation: BenchmarkImplementation,
                            val metric: BenchmarkMetric, val data: Any?)