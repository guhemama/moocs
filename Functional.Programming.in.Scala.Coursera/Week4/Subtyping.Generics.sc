
/**
  * This function takes some type S, which must be a subtype of
  * IntSet, and returns the same type.
  *
  * S <: T means S is a subtype of T.
  * S >: T means S is a supertype of T, or T is a subtype of S.
  *
  * [S :> NonEmpty <: IntSet] restricts the type S to any type
  * between the interval NonEmpty and IntSet.
  */
def assertAllPos[S <: IntSet](someSet: S): S = ???