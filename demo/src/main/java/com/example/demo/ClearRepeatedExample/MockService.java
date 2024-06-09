package com.example.demo.ClearRepeatedExample;

public class MockService implements Service {
    @Override
    public void sendBook() {
        System.out.println("Sending book...");
        // 模拟异常
//        throw new RuntimeException("Error sending book");
    }

    @Override
    public void sendChapter() {
        System.out.println("Sending chapter...");
    }

    @Override
    public void startTranslation() {
        System.out.println("Starting translation...");
    }
}