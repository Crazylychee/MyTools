package com.example.demo.ClearRepeatedExample;

public class MockNotification extends Notification {
    @Override
    public void send(SendFailure failure) {
        System.out.println("Sending notification: " + failure.getCause());
    }
}