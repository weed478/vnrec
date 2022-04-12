package carbon.vnrec.db

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class MockDataProvider (sc: SparkContext)
  extends DataProvider {

  override val vn: RDD[String] = sc.parallelize(Seq(
    "v102\tja\tcv49761\t5235146\t244\t318\t761\t4\tMoonlight Carnival\\nCarnival of moonlight\\ncarnevale della luce della luna\\n月光嘉年华\t\tThe story\t793",
    "v115\tja\tcv162\t5050857\t270\t206\t596\t4\tAfter School Romance Club\\nCRC\\nHRC\\n放課後恋愛倶楽部\t\tYou are\t591",
  ))

  override val vn_titles: RDD[String] = sc.parallelize(Seq(
    "v102\tja\t月光のカルネヴァーレ\tGekkou no Carnevale\tt",
    "v115\ten\tCasual Romance Club -L'etude de l'amour-\t\\N\tt",
    "v115\tja\t放課後恋愛クラブ ～恋のエチュード～\tHoukago Ren'ai Club ~Koi no Etude~\tt",
  ))

  override val users: RDD[String] = sc.parallelize(Seq(
    "u373\tf\tt\tt\trothen\tt",
    "u391\tf\tt\tt\tz-2488\tt",
  ))

  override val ulist_vns: RDD[String] = sc.parallelize(Seq(
    "u2\tv18770\t2018-04-26 00:00:00+00\t2018-04-28 00:00:00+00\t2018-04-28 00:00:00+00\t\\N\t\\N\t60\t",
    "u5\tv432\t2015-09-05 00:00:00+00\t2015-09-05 00:00:00+00\t\\N\t\\N\t\\N\t\\N\t",
  ))

  override val tags: RDD[String] = sc.parallelize(Seq(
    "g3435\tcont\t0\tt\tt\tReligious School\tThis visual novel\t",
    "g3312\tcont\t0\tt\tt\tSupport Character Based on Real Person\tOne or more\tSide Character Based on Real Person",
  ))

  override val tags_vn: RDD[String] = sc.parallelize(Seq(
    "g2\tv11\tu70739\t3\t\\N\t2015-09-12 00:00:00+00\tf\t",
    "g2\tv428\tu15\t-2\t\\N\t2010-12-15 00:00:00+00\tf\t",
  ))
}
