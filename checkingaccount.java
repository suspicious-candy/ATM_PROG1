import java.io.Serializable;
//////////////// FILE HEADER (INCLUDE IN EVERY FILE) //////////////////////////
//
// Title:    ATM Program
// Author:   Shreyansh jain
// Email:    jainshreyansh606@gmail.com
//
/////////////////////////////////////////////////// //////////////////////////

import java.io.Serializable;
// This class represents a checking account and implements the Serializable interface
public class checkingaccount implements Serializable {
    // This is the balance of the checking account
    int Balance;

    // This constructor initializes the checking account with a specific balance
    public checkingaccount(int balance) {
        // If the initial balance is less than 0, an exception is thrown
        if(balance<0){
            throw  new IllegalArgumentException("Wrong balance");
        }
        // The balance of the account is set to the initial balance
        this.Balance=balance;
    }

    // This constructor initializes the checking account with a balance of 0
    public checkingaccount(){
        this.Balance=0;
    }

    // This method returns the current balance of the account
    public int getBalance() {
        return Balance;
    }

    // This method adds a specified amount to the account balance
    public void addBalance(int a){
        // If the amount to be added is less than 0, an exception is thrown
        if(a<0){
            throw new IllegalArgumentException("illegal amount");
        }
        // The amount is added to the balance
        Balance+=a;
    }

    // This method withdraws a specified amount from the account balance
    public void withdraw(int a){
        // If the amount to be withdrawn is greater than the balance, an exception is thrown
        if(a>getBalance()){
            throw new IllegalArgumentException("Illegal amount");
        }
        // The amount is subtracted from the balance
        Balance-=a;
    }
}