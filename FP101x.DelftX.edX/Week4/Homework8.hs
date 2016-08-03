putStr' :: String -> IO ()
putStr' [] = return ()
putStr' (x:xs) = putChar x >> putStr' xs

putStrLn' :: String -> IO ()
putStrLn' [] = putChar '\n'
putStrLn' xs = putStr' xs >> putStrLn ""

-- Which of the following definitions implements the function, that takes a
-- "predicate" of type Monad m => a -> m Bool and uses this to filter a finite,
-- non-partial list of non-bottom elements of type a.
filterM' :: Monad m => (a -> m Bool) -> [a] -> m [a]
filterM' _ [] = return []
filterM' p (x:xs)
  = do flag <- p x
       ys <- filterM' p xs
       if flag then return ys else return (x : ys)
