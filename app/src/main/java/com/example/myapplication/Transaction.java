package com.example.myapplication;

import java.util.ArrayList;

public class Transaction {

    private ArrayList<Bill> bills;
    private ArrayList<Payment> payments;
    private boolean status;

    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTotalAmount(){
        int totalAmount=0;
        for(int i=0; i<bills.size(); i++){
            totalAmount+=Integer.parseInt(bills.get(i).getAmount());
        }
        return totalAmount;
    }
}
