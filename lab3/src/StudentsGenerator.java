class StudentsGenerator implements Runnable {
    private StudentQueue queue;

    StudentsGenerator(StudentQueue queue) {
        this.queue = queue;
    }

    public void run() {
        while (true)
            try {
                queue.put();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
