import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class StudentQueue {
    private Queue<Student> queue = new LinkedList<>();
    private final int maxAmount = 10;
    static class Student {
        int labsCount;
        Subject subject;

        Student(int labsCount, Subject subject) {
            this.labsCount = labsCount;
            this.subject = subject;
        }
        static Student getRandomStudent() {
            int[] labs = new int[]{10, 20, 100};
            int n = (int) Math.floor(Math.random() * labs.length);
            return(new Student(labs[n],  Subject.values()[new Random().nextInt(Subject.values().length)]));
        }
        @Override
        public String toString() {
            return (subject + " | " + labsCount + "лаб");
        }
    }
    public synchronized Student get(Subject subject) throws InterruptedException {
        if (queue.size() < 8) {
            wait();
            return null;
        } else if (queue.peek().subject == subject) {
            notifyAll();
            Student student = queue.poll();
            System.out.println("[robot] Robot " + subject + " взял на проверку: " + student.toString());
            return student;
        }
        return null;
    }

    public synchronized void put() throws InterruptedException{
        while (queue.size() >= maxAmount) {
            wait();
        }
        Student student = Student.getRandomStudent();
        queue.add(student);
        System.out.println("[student] Пришел новый студент " + student.toString());
        System.out.println("[info] В очереди теперь " + queue.size());
        for (var i : queue) {
            System.out.println("[queue] " + i.toString());
        }

        notify();
    }
}