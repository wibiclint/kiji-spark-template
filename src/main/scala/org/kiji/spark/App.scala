package org.kiji.spark

import org.apache.spark.SparkContext
import SparkContext._
import org.rogach.scallop._

/**
 * Main class for running a simple Spark job.
 */
object App {

  // Parse command-line options
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val inputFile = opt[String](required = true)
    val host = opt[String](default = Some("local"))
  }

  /**
   * Trivial main class that does word count (yawn...).
   * @param args Command-line arguments.
   */
  def main(args: Array[String]) {

    val conf = new Conf(args)
    val inputFile = conf.inputFile()
    val hostName = conf.host()
    println("input file = " + inputFile)
    println("host     = " + hostName)

    // Create the SparkContext for this application.
    val sc = new SparkContext(hostName, "Word count!!!!!!!!")

    val lines = sc.textFile(inputFile)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    val results = wordCounts.take(5)
    results.foreach(println)
  }
}

