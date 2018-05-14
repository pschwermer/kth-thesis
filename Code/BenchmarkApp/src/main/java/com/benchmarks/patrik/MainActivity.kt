package com.benchmarks.patrik

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter
import java.lang.IllegalArgumentException
import java.lang.Math.floor
import android.os.AsyncTask.execute
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var isBenchmarking = false
    private var benchmarkHandler: BenchmarkHandler? = null
    private var benchmarkHandlerThread: HandlerThread? = null
    private var benchmarkResults: ArrayList<BenchmarkResult> = arrayListOf()
    private var maxIterations = 0
    private var currentIteration = 0

    private val uiHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                BenchmarkStatus.UPDATE_PROGRESS.id -> {
                    val progressBarValue: Int = msg.obj as Int
                    setProgressBar(progressBarValue)
                }

                BenchmarkStatus.UPDATE_MESSAGE.id -> {
                    val statusMessage: String = msg.obj as String
                    setBenchmarkMessage(statusMessage)
                }

                BenchmarkStatus.BENCHMARK_FINISHED.id -> {
                    benchmarkResults.add(msg.obj as BenchmarkResult)

                    if (currentIteration < maxIterations - 1) {
                        currentIteration++
                        setProgressBar(floor((currentIteration.toDouble() / maxIterations) * 100).toInt())
                    } else if (maxIterations != 0) {
                        if(getBenchmarkMetric() != BenchmarkMetric.BOXING_OF_PRIMITIVES) saveBenchmarkData(benchmarkResults)
                        stopBenchmarking("Benchmark finished successfully")
                        benchmarkResults = arrayListOf()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        populateSpinners()

        //Setup benchmark handler thread
        val benchmarkHandlerThread = HandlerThread("Benchmark Handler Thread", Process.THREAD_PRIORITY_FOREGROUND)
        benchmarkHandlerThread.start()
        benchmarkHandler = BenchmarkHandler(benchmarkHandlerThread.looper, uiHandler)

        addEventListeners()
        requestStoragePermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        benchmarkHandlerThread?.quit() //Close benchmark handler thread.
    }

    /**
     * Populates the spinners with the data available in "strings.xml"
     */
    private fun populateSpinners() {
        val spinners = arrayOf(
                Pair(this.SpinnerBenchmark, resources.getStringArray(R.array.SpinnerBenchmarkArray)),
                Pair(this.SpinnerImplementation, resources.getStringArray(R.array.SpinnerImplementationArray)),
                Pair(this.SpinnerMetric, resources.getStringArray(R.array.SpinnerMetricArray)))

        spinners.forEach { (spinner, spinnerData) ->
            spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData)
        }
    }

    /**
     * Adds event listeners on:
     * - ButtonBenchmark (start/stop benchmarking)
     */
    private fun addEventListeners() {
        ButtonBenchmark.setOnClickListener {
            if (isBenchmarking) {
                confirmationDialogStopBenchmarking()
            } else {
                startBenchmarking()
            }
        }
    }

    /**
     * Updates the UI to prepare for benchmarking and sends benchmark messages to benchmarkHandler
     */
    private fun startBenchmarking() {
        disableInput()
        isBenchmarking = true
        setBenchmarkMessage("Benchmark in progress...")

        try{
            maxIterations = getBenchmarkIterations()
        } catch (e: IllegalArgumentException){
            e.printStackTrace()
            maxIterations = 1
        }

        currentIteration = 0

        for (i in 0 until maxIterations) {
            benchmarkHandler?.obtainMessage()!!.apply {
                what = BenchmarkStatus.START_BENCHMARK.id
                obj = createBenchmarkMessage(i)
            }.sendToTarget()
        }
    }

    /**
     * Creates and AsyncTask LogResultsToFileTask in order to save the [results] to file.
     */
    private fun saveBenchmarkData(results: ArrayList<BenchmarkResult>) {
        var fileName: String = generateFileName(null)
        val dateAndTime = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())
        fileName += "$dateAndTime.txt"

        LogResultsToFileTask().execute(Pair(results, fileName))
        setBenchmarkMessage("Results saved to: \n'$fileName'")
    }

    /**
     * Empties the benchmark message queue of benchmarkHandler, updates the UI to signal that
     * benchmarking has stopped. Sets the benchmark message to [benchmarkMessage]
     */
    private fun stopBenchmarking(benchmarkMessage: String) {
        benchmarkHandler?.removeMessages(BenchmarkStatus.START_BENCHMARK.id)
        benchmarkResults = arrayListOf()
        isBenchmarking = false
        maxIterations = 0
        currentIteration = 0
        setBenchmarkMessage(benchmarkMessage)
        setProgressBar(0)
        enableInput()
    }

    /**
     * Disables user input (apart from ButtonBenchmark) and updates ButtonBenchmark to say "Stop"
     */
    private fun disableInput() {
        ButtonBenchmark.text = "Stop"
        SpinnerBenchmark.isEnabled = false
        SpinnerImplementation.isEnabled = false
        SpinnerMetric.isEnabled = false
        TextIterations.isEnabled = false
    }

    /**
     * Enables user input and updates ButtonBenchmark to say "Benchmark"
     */
    private fun enableInput() {
        ButtonBenchmark.text = "Benchmark"
        SpinnerBenchmark.isEnabled = true
        SpinnerImplementation.isEnabled = true
        SpinnerMetric.isEnabled = true
        TextIterations.isEnabled = true
    }

    /**
     * Generates a file name based on current benchmark instance
     * @return String - The generated file name
     */
    private fun generateFileName(iteration: Int?): String{
        val sb: StringBuilder = StringBuilder()

        when(getBenchmarkType()){
            BenchmarkType.FASTA -> sb.append("FA")
            BenchmarkType.FANNKUCH_REDUX -> sb.append("FR")
            BenchmarkType.N_BODY -> sb.append("NB")
            BenchmarkType.REVERSE_COMPLEMENT -> sb.append("RC")
        }

        sb.append("-")

        when(getBenchmarkImplementation()){
            BenchmarkImplementation.JAVA -> sb.append("J")
            BenchmarkImplementation.KOTLIN_CONVERTED -> sb.append("KC")
            BenchmarkImplementation.KOTLIN_IDIOMATIC -> sb.append("KI")
        }

        sb.append("-")

        when(getBenchmarkMetric()){
            BenchmarkMetric.BOXING_OF_PRIMITIVES -> sb.append("BP-$iteration")
            BenchmarkMetric.RUNTIME -> sb.append("RT")
            BenchmarkMetric.MEMORY_CONSUMPTION -> sb.append("MC")
            BenchmarkMetric.GARBAGE_COLLECTION -> sb.append("GC")
        }

        sb.append("--")

        return sb.toString()
    }

    /**
     * Updates ProgressBarIterations with [value]
     */
    private fun setProgressBar(value: Int) {
        ProgressBarIterations.progress = value
    }

    /**
     * Sets TextBenchmarkMessage to [msg]
     */
    private fun setBenchmarkMessage(msg: String) {
        TextBenchmarkMessage.text = msg
    }

    /**
     * Creates a BenchmarkMessage to be sent to the BenchmarkHandler based on the user input or
     * a default message in the case of invalid user input.
     * @return BenchmarkMessage - The message to send to the benchmark handler
     */
    private fun createBenchmarkMessage(iteration: Int): BenchmarkMessage {
        var message: BenchmarkMessage
        try{
            /*
               Handle the special case of boxed primitives, where the result is saved on the
               BenchmarkHandlerThread.
             */
            val benchmarkMetric = getBenchmarkMetric()
            val logIterations = arrayOf(0,49,99)
            var data: String? = null
            if(benchmarkMetric == BenchmarkMetric.BOXING_OF_PRIMITIVES && logIterations.contains(iteration)){
                data = generateFileName(iteration+1)
            }

            message = BenchmarkMessage(getBenchmarkType(), getBenchmarkImplementation(), getBenchmarkMetric(), data)
        } catch (e: IllegalArgumentException){
            e.printStackTrace()
            message = BenchmarkMessage(BenchmarkType.FASTA, BenchmarkImplementation.JAVA, BenchmarkMetric.RUNTIME, null)
        }

        return message
    }

    /**
     * Returns the selected value of SpinnerBenchmark as a BenchmarkType
     * @throws IllegalArgumentException - In case of invalid user input
     * @return BenchmarkType - The benchmark type which the user has selected
     */
    private fun getBenchmarkType(): BenchmarkType {
        val selectedValue = this.SpinnerBenchmark.selectedItem.toString()
        when (selectedValue) {
            "Fasta" -> return BenchmarkType.FASTA
            "Fannkuch-Redux" -> return BenchmarkType.FANNKUCH_REDUX
            "N-body" -> return BenchmarkType.N_BODY
            "Reverse-Complement" -> return BenchmarkType.REVERSE_COMPLEMENT
            else -> throw IllegalArgumentException("Invalid spinner value for 'Benchmark type'")
        }
    }

    /**
     * Returns the selected value of SpinnerImplementation as a BenchmarkImplementation
     * @throws IllegalArgumentException - In case of invalid user input
     * @return BenchmarkImplementation - The benchmark implementation which the user has selected
     */
    private fun getBenchmarkImplementation(): BenchmarkImplementation {
        val selectedValue = this.SpinnerImplementation.selectedItem.toString()
        when (selectedValue) {
            "Java" -> return BenchmarkImplementation.JAVA
            "Kotlin converted" -> return BenchmarkImplementation.KOTLIN_CONVERTED
            "Kotlin idiomatic" -> return BenchmarkImplementation.KOTLIN_IDIOMATIC
            else -> throw IllegalArgumentException("Invalid spinner value for 'Benchmark implementation'")
        }
    }

    /**
     * Returns the selected value of SpinnerMetric as a BenchmarkMetric
     * @throws IllegalArgumentException - In case of invalid user input
     * @return BenchmarkMetric - The benchmark metric which the user has selected
     */
    private fun getBenchmarkMetric(): BenchmarkMetric {
        val selectedValue = this.SpinnerMetric.selectedItem.toString()
        when (selectedValue) {
            "Boxing of primitives" -> return BenchmarkMetric.BOXING_OF_PRIMITIVES
            "Garbage collection" -> return BenchmarkMetric.GARBAGE_COLLECTION
            "Memory consumption" -> return BenchmarkMetric.MEMORY_CONSUMPTION
            "Runtime" -> return BenchmarkMetric.RUNTIME
            else -> throw IllegalArgumentException("Invalid spinner value for 'Benchmark metric'")
        }
    }

    /**
     * Returns the selected number of iterations as an Int
     * @throws IllegalArgumentException - In case of invalid user input
     * @return Int - The number benchmark iterations which the user has selected
     */
    private fun getBenchmarkIterations(): Int {
        val enteredValue = this.TextIterations.text.toString().toInt()
        when (enteredValue) {
            in 1..110 -> return enteredValue
            else -> throw IllegalArgumentException("Invalid numeric value for 'Benchmark iterations' (1-110)")
        }
    }

    /**
     * Displays a dialog to confirm the stop of a current benchmark. In the case when the user
     * positively confirms the stop, stopBenchmark() is called. Otherwise, the dialog is dismissed.
     */
    private fun confirmationDialogStopBenchmarking() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Stop benchmark")
        alertDialog.setMessage("Are you sure you want to stop benchmarking?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES") { _, _ ->
            stopBenchmarking("Benchmark was aborted, no data saved!")
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.show()
    }

    /**
     * Asks the user for permission to write to external storage (needed to save the benchmark data)
     */
    private fun requestStoragePermission(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }
}
