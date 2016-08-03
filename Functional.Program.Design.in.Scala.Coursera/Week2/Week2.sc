def streamRange(lo: Int, hi: Int): Stream[Int] = {
  if (lo >= hi) Stream.empty
  else Stream.cons(lo, streamRange(lo + 1, hi))
}

streamRange(1, 10)

def from(n: Int): Stream[Int] = n #:: from(n + 1)

val naturalNumbers = from(0)
val multiplesOf4 = naturalNumbers map (_ * 4)

(multiplesOf4 take 10).toList