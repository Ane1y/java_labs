class Robot implements Runnable {

    private StudentQueue queue;
    private Subject subject;

    Robot(StudentQueue cabinet, Subject subject) {
        System.out.println("[robot] Робот " + subject + " готов к работе");
        this.queue = cabinet;
        this.subject = subject;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.currentThread().setName("ROBOT " + subject);
                Thread.sleep(500);
                StudentQueue.Student student = queue.get(subject);
                if (student != null) {
                    while (student.labsCount > 0) {
                        Thread.sleep(500);
                        System.out.println("[info] " + Thread.currentThread().getName() + " осталось проверить " + student.toString());
                        student.labsCount -= 5;
                    }
                    Thread.sleep(500);
                    System.out.println("[robot] " + Thread.currentThread().getName() + " освободился");
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}