package org.kiji.spark

import org.apache.hadoop.conf.{Configured, Configuration}
import org.apache.hadoop.mapred.{InputFormat, JobConf}
import org.kiji.mapreduce.framework.{KijiConfKeys, KijiTableInputFormat}
import org.apache.hadoop.mapred.lib.NullOutputFormat
import org.apache.commons.codec.binary.Base64
import org.kiji.schema._
import scala.Some
import org.apache.commons.lang.SerializationUtils

/**
 * Something like KijiTap and KijiSchema in KijiExpress.
 *
 * Obviously in the future this needs some real methods for builders, etc.
 */
case class KijiConf(val kijiURI: KijiURI) {

  def createJobConf(): JobConf = {

    // Should pull out any settings from the classpath.
    val configuration = new Configuration()

    val conf: JobConf = new JobConf(configuration)

    // Set the input format.
    //conf.setInputFormat(classOf[KijiTableInputFormat])
    conf.setInputFormat(classOf[InputFormat[EntityId, KijiRowData]])

    // Store the input table.
    conf.set(KijiConfKeys.KIJI_INPUT_TABLE_URI, kijiURI.toString)

    // Get a reference to the Kiji table to build a data request.
    //val kiji: Kiji = Kiji.Factory.open(kijiURI, conf)

    // Let's just hard-code one for now.
    val builder: KijiDataRequestBuilder = KijiDataRequest.builder()

    builder
        .newColumnsDef()
        .addFamily("info")

    val dataRequest: KijiDataRequest = builder.build()

    // Set data request.
    conf.set(
      KijiConfKeys.KIJI_INPUT_DATA_REQUEST,
      Base64.encodeBase64String(SerializationUtils.serialize(dataRequest)))

    conf.setUserClassesTakesPrecedence(true);

    conf
  }

  // Also: Row filter, start EID, end EID

  // For sink:
  // Configure the job's output format.
  //conf.setOutputFormat(classOf[NullOutputFormat[_, _]])

  // Store the output table.
  //conf.set(KijiConfKeys.KIJI_OUTPUT_TABLE_URI, tableUri)
}

