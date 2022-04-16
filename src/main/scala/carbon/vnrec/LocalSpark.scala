package carbon.vnrec

import org.apache.spark.{SparkConf, SparkContext}

object LocalSpark {
  def getOrCreate(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("local[8]")
      .setAppName("vnrec")
    val sc = SparkContext.getOrCreate(conf)
    sc.setLogLevel("WARN")
    sc
  }
}
