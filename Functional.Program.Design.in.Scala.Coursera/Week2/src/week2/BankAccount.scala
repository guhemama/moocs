package week2

/**
  * Created by gustavo on 15/07/16.
  */
class BankAccount {
  protected var balance = 0

  def deposit(amount: Int): Unit = {
    if (amount > 0) balance = balance + amount
  }

  def withdraw(amount: Int): Int =
    if (0 < amount && amount <= balance) {
      balance = balance - amount
      balance
    } else throw new Error("Insuficient funds")
}
