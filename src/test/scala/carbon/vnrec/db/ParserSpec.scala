package carbon.vnrec.db

import carbon.vnrec.UnitSpec

class ParserSpec extends UnitSpec {

  private val tag = Tag("g12\tcont\t0\tt\tf\tAction\tThis game")
  private val tagVn = TagVn("g142\tv71\tu7104\t3\t1\t2010-12-15 00:00:00+00\tf\t")
  private val user = User("u105\tf\tt\tt\tdeathscythe86\tt")
  private val userVn1 = UserVn("u5\tv45\t2008-09-22 00:00:00+00\t2008-09-22 00:00:00+00\t\\N\t\\N\t\\N\t\\N\t")
  private val userVn2 = UserVn("u5\tv45\t2008-09-22 00:00:00+00\t2008-09-22 00:00:00+00\t\\N\t\\N\t\\N\t70\t")
  private val vn = Vn("v35\tja\tcv38699\t31332\t895\t697\t662\t3\t\t\tThat summer day\t665")
  private val vnTitle = VnTitle("v21\tja\t天使のいない12月\tTenshi no Inai 12-gatsu\tt")

  "Tag" should "parse id" in assert(Id(tag.id) === "g12")
  it should "parse name" in assert(tag.name === "Action")
  it should "parse category" in assert(tag.cat === ContentTag)

  "TagVn" should "parse tag id" in assert(Id(tagVn.tag) === "g142")
  it should "parse vn id" in assert(Id(tagVn.vid) === "v71")
  it should "parse vote" in assert(tagVn.vote === 3)
  it should "parse spoiler" in assert(tagVn.spoiler === Some(MinorSpoiler))

  "User" should "parse id" in assert(Id(user.id) === "u105")
  it should "parse username" in assert(user.username === "deathscythe86")

  "UserVn" should "ignore invalid votes" in assert(userVn1.isEmpty)
  it should "parse uid" in assert(Id(userVn2.get.uid) === "u5")
  it should "parse vid" in assert(Id(userVn2.get.vid) === "v45")
  it should "parse vote" in assert(userVn2.get.vote === 70)

  "Vn" should "parse id" in assert(Id(vn.id) === "v35")
  it should "parse popularity" in assert(vn.c_popularity === 697)
  it should "parse rating" in assert(vn.c_rating.get === 662)
  it should "parse average" in assert(vn.c_average.get === 665)

  "VnTitle" should "parse vn id" in assert(Id(vnTitle.id) === "v21")
  it should "parse lang" in assert(vnTitle.lang === "ja")
  it should "parse title" in assert(vnTitle.title === "天使のいない12月")
  it should "parse latin" in assert(vnTitle.latin === "Tenshi no Inai 12-gatsu")
  it should "parse official" in assert(vnTitle.official === true)

}
