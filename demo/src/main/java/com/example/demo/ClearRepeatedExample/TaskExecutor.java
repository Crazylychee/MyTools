package com.example.demo.ClearRepeatedExample;

public class TaskExecutor {
    private final Notification notification;
    private final Service service;

    public TaskExecutor(Notification notification, Service service) {
        this.notification = notification;
        this.service = service;
    }

    private void executeTask(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            notification.send(new SendFailure(t));
            throw t;
        }
    }

    @Task
    public void sendBook() {
        executeTask(service::sendBook);
    }

    @Task
    public void sendChapter() {
        executeTask(service::sendChapter);
    }

    @Task
    public void startTranslation() {
        executeTask(service::startTranslation);
    }
}