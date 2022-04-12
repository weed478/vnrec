package carbon.vnrec

import org.apache.spark.{SparkConf, SparkContext}

abstract class SparkSpec extends UnitSpec {
  protected val sc: SparkContext = {
    val conf = new SparkConf()
      .setMaster("local[4]")
      .setAppName("vnrec-test")
    val sc = SparkContext.getOrCreate(conf)
    sc.setLogLevel("WARN")
    sc
  }
}
