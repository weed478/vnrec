package carbon.vnrec.db

import carbon.vnrec.UnitSpec

class IdSpec extends UnitSpec {
  "VN id" should "parse both ways" in {
    assert(Id(Id("v123")) === "v123")
  }

  "User id" should "parse both ways" in {
    assert(Id(Id("u123")) === "u123")
  }

  "Tag id" should "parse both ways" in {
    assert(Id(Id("g123")) === "g123")
  }
}
