package com.example.demo.ClearRepeatedExample;

public class Notification {

    public void send(SendFailure e) {
        System.out.println("Send notification: " + e.getMessage());
    }
}
