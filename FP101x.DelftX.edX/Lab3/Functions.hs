module Lab3 where

-----------------------------------------------------------------------------------------------------------------------------
-- LIST COMPREHENSIONS
------------------------------------------------------------------------------------------------------------------------------

-- ===================================
-- Ex. 0 - 2
-- Using a list comprehension, define a function that selects all the even numbers from a list.
-- ===================================
evens :: [Integer] -> [Integer]
evens xs = [x | x <- xs, even x]

-- ===================================
-- Ex. 3 - 4
-- Using a list comprehension, define a function squares that takes a non-bottom
-- Integer n >= 0 as its argument and returns a list of the numbers [1..n] squared.
-- ===================================
squares :: Integer -> [Integer]
squares n = [x ^ 2 | x <- [1..n]]

sumSquares :: Integer -> Integer
sumSquares n = sum (squares n)

---- ===================================
---- Ex. 5 - 7
-- Modify the previous definition of squares such that it now takes two
-- non-bottom Integer arguments, m >= 0 and n >= 0 and returns a list of the
-- m square numbers that come after the first n square numbers.
---- ===================================
squares' :: Integer -> Integer -> [Integer]
squares' m n = [x ^ 2 | x <- list]
  where list = [(n + 1) .. (n + m)]

sumSquares' :: Integer -> Integer
sumSquares' x = sum . uncurry squares' $ (x, x)

---- ===================================
---- Ex. 8
---- ===================================

coords :: Integer -> Integer -> [(Integer,Integer)]
coords m n = [(x, y) | x <- [0..m], y <- [0..n]]