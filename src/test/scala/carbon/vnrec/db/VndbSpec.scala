package carbon.vnrec.db

import carbon.vnrec.SparkSpec

class VndbSpec extends SparkSpec {

  private val db = new Vndb(new MockDataProvider(sc))

  "VNDB" should "load VNs" in {
    assert(db.vn.filter(_.id == Id("v102")).count() === 1)
    assert(db.vn.filter(_.id == Id("v115")).count() === 1)
  }

  it should "load VN titles" in {
    assert(db.vn_titles.filter(_.id == Id("v102")).count() === 1)
    assert(db.vn_titles.filter(_.id == Id("v115")).count() === 2)
  }

  it should "load users" in {
    assert(db.users.filter(_.id == Id("u373")).count() === 1)
    assert(db.users.filter(_.id == Id("u391")).count() === 1)
  }

  it should "load user VNs" in
    assert(db.ulist_vns
      .filter(u => u.uid == Id("u2") && u.vid == Id("v18770"))
      .count() === 1)

  it should "not load invalid votes" in
    assert(db.ulist_vns
      .filter(u => u.uid == Id("u5") && u.vid == Id("v432"))
      .count() === 0)

  it should "load tags" in {
    assert(db.tags.filter(_.id == Id("g3435")).count() === 1)
    assert(db.tags.filter(_.id == Id("g3312")).count() === 1)
  }

  it should "load tag VNs" in {
    val t1 = db.tags_vn
      .filter(_.tag == Id("g2"))
      .filter(_.vid == Id("v11"))
      .first()
    val t2 = db.tags_vn
      .filter(_.tag == Id("g2"))
      .filter(_.vid == Id("v428"))
      .first()
    assert(t1.vote === 3)
    assert(t2.vote === -2)
  }
}
