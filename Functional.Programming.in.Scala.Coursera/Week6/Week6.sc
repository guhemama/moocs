object Week6 {
  val xs = Array(1, 3, 9, 21)
  val s: String = "Hello World"
  s.filter (_.isUpper) // HW; equivalent to s.filter (c => c.isUpper)

  s.exists (_.isUpper) // true
  s.forall (_.isUpper) // false

  List(1,2,3) zip s // List((1,H), (2,e), (3,l))

  xs.sum // 34
  xs.product // 567


}