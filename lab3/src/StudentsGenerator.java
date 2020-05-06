class StudentsGenerator extends Thread {
    private StudentQueue queue;

    StudentsGenerator(StudentQueue queue) {
        this.queue = queue;
        this.setName("STUDENTS");
    }

    public void run() {
        while (true) {
            try {
                if (!queue.isClosed()) {
                    queue.put();
                } else {
                    System.out.println("Студенты закончились");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
