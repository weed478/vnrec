package carbon.vnrec.db

import carbon.vnrec.UnitSpec

class IdSpec extends UnitSpec {
  "ID" should "parse both ways" in {
    for (p <- Id.prefixes; i <- 0 to 100) {
      val id = s"$p$i"
      assert(Id(Id(id)) === id)
    }
  }

  it should "be unique" in {
    for (p1 <- Id.prefixes; p2 <- Id.prefixes;
         i1 <- 0 to 100; i2 <- 0 to 100) {
      val id1 = s"$p1$i1"
      val id2 = s"$p2$i2"
      assert((id1 == id2) === (Id(id1) == Id(id2)))
    }
  }
}
