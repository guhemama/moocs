diff :: String -> String -> String
diff xs ys = [if elem x ys then x else '-' | x <- xs]