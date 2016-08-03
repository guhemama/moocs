import Prelude hiding (return)
import Data.Char

type Parser a = String -> [(a, String)]

-- This parser consumes the first char and returns a sigleton list
item :: Parser Char
item = \input -> case input of
                 []     -> []
                 (x:xs) -> [(x, xs)]

-- This parser always fails
failure :: Parser a
failure = \input -> []

-- This parser always succeedsa
return :: a -> Parser a
return v = \input -> [(v, input)]

parse :: Parser a -> String -> [(a, String)]
parse p input = p input

-- Parsing a character that satisfies a predicate
--satisfies :: (Char -> Bool) -> Parser Char
--satisfies p = do x <- item
--                 if p x then return x else failure