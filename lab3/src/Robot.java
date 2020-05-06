class Robot extends Thread {

    private StudentQueue queue;
    private Subject subject;

    Robot(StudentQueue cabinet, Subject subject) {
        System.out.println("[robot] Робот " + subject + " готов к работе");
        this.queue = cabinet;
        this.setName("ROBOT " + subject);
        this.subject = subject;
    }

    @Override
    public void run() {
        do {
            try {
                StudentQueue.Student student = queue.get(subject);
                if (student != null) {
                    while (student.labsCount > 0) {
                        sleep(10);
                        student.labsCount -= 5;
                        System.out.println("[info] " + getName() + " осталось проверить " + student.toString());
                    }
                    System.out.println("[robot] " + getName() + " освободился");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!queue.isClosed() && (queue.getSize() > 0));
        System.out.println("[robot]" + getName() + " закончил работу");
    }
}