package com.example.demo.ClearRepeatedExample;

public class Test {

    public static void main(String[] args) {
        Notification notification = new MockNotification();
        Service service = new MockService();
        TaskExecutor taskExecutor = new TaskExecutor(notification, service);

        taskExecutor.sendBook();
        taskExecutor.sendChapter();
        taskExecutor.startTranslation();
    }
}
