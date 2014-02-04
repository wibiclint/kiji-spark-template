package org.kiji.spark

import org.apache.spark.SparkContext
import SparkContext._
import org.rogach.scallop._
import org.kiji.mapreduce.framework.{KijiConfKeys, KijiTableInputFormat}
import org.kiji.schema.{KijiURI, KijiRowData, EntityId}
import org.apache.hadoop.conf.Configuration
import org.apache.spark.rdd.NewHadoopRDD
import org.apache.hadoop.mapreduce.lib.output.{TextOutputFormat, FileOutputFormat}
import org.apache.hadoop.mapred.JobConf
import org.kiji.mapreduce.impl.{DirectKijiTableWriterContext, HFileWriterContext}
import org.kiji.mapreduce.KijiTableContext

/**
 * Main class for running a simple Spark job.
 */
object App {

  // Parse command-line options
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val inputFile = opt[String](default = Some("foo.txt"))
    val host = opt[String](default = Some("local"))
  }

  /**
   * Trivial main class that does word count (yawn...).
   * @param args Command-line arguments.
   */
  def main(args: Array[String]) {

    val conf = new Conf(args)
    //val inputFile = conf.inputFile()
    val hostName = conf.host()
    //println("input file = " + inputFile)
    println("host     = " + hostName)

    // Create the SparkContext for this application.
    val sc = new SparkContext(hostName, "Spark + Kiji")

    // Create a Hadoop Configuration instance for getting data out of Kiji
    val kijiURI: KijiURI = KijiURI.newBuilder("kiji://localhost:2181/default/users/").build()
    val kijiConf: KijiConf = KijiConf(kijiURI)

    // Create a Kiji RDD
    /*
    val kijiStuff = sc.newAPIHadoopRDD(
      kijiConf.createJobConf(),
      // InputFormat class
      classOf[KijiTableInputFormat],
      // Key class
      classOf[EntityId],
      // Value class
      classOf[KijiRowData])
      */

    val kijiStuff = new NewHadoopRDD(
      sc = sc,
      // InputFormat class
      inputFormatClass = classOf[KijiTableInputFormat],
      // Key class
      keyClass = classOf[EntityId],
      // Value class
      valueClass = classOf[KijiRowData],
      conf = kijiConf.createJobConf(),
      cloneRecords = false)
    val results = kijiStuff.take(2)
    results.foreach(println)

    val kijiKeyValue = kijiStuff.map {
      rowTup: (EntityId, KijiRowData)  => {
        val rowData: KijiRowData = rowTup._2
        val userIdMap = rowData.getValues("info", "name")
        val eid = rowTup._1
        (eid.getComponents().get(0), userIdMap.values().toArray().head)
      }
    }


    // Write to a text file
    kijiKeyValue.saveAsNewAPIHadoopFile(
        path = "spark_output",
        keyClass = classOf[String],
        valueClass = classOf[String],
        //outputFormatClass = classOf[FileOutputFormat[String, String]],
        outputFormatClass = classOf[TextOutputFormat[String, String]],
        conf = kijiConf.createOutputJobConf()
      )

    // Should pull out any settings from the classpath.
    val wConf: JobConf = new JobConf(new Configuration())
    wConf.setClass(KijiConfKeys.KIJI_TABLE_CONTEXT_CLASS, classOf[DirectKijiTableWriterContext], classOf[KijiTableContext])
    wConf.setBoolean("mapred.map.tasks.speculative.execution", false)
    wConf.set(KijiConfKeys.KIJI_OUTPUT_TABLE_URI, kijiURI.toString)

    /*
    val lines = sc.textFile(inputFile)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    val results = wordCounts.take(5)
    results.foreach(println)
    */
  }
}

