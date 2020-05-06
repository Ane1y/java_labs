import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

class StudentQueue {
    private Map<Subject, Queue<Student>> queue = new LinkedHashMap<>(3);

    private int numOfStudents;
    private boolean end = false;

    static class Student {
        String name;
        static int counter = 0;
        int labsCount;
        private Subject subject;

        Student(int labsCount, Subject subject) {
            this.labsCount = labsCount;
            this.subject = subject;
            counter++;
            name = "student" + counter;
        }

        static Student getRandomStudent() {
            int[] labs = new int[]{10, 20, 100};
            int n = (int) Math.floor(Math.random() * labs.length);
            return (new Student(labs[n], Subject.values()[new Random().nextInt(Subject.values().length)]));
        }

        @Override
        public String toString() {
            return (name + ", " + subject + " | " + labsCount + "лаб");
        }
    }

    StudentQueue(int num) {
        for (Subject subject : Subject.values()) {
            queue.put(subject, new ConcurrentLinkedQueue<>());
        }
        numOfStudents = num;
    }

    public boolean isClosed() {
        return end;
    }

    public int getSize() {
        int size = 0;
        for (var sub : Subject.values()) {
            size += queue.get(sub).size();
        }
        return size;
    }

    public synchronized Student get(Subject subject) throws InterruptedException {
        if (getSize() < 2) {
            wait();
            return null;
        } else if (!queue.get(subject).isEmpty()) {
            notifyAll();
            Student student = queue.get(subject).poll();
            System.out.println("[robot] Robot " + subject + " взял на проверку: " + student.toString());
            return student;
        }
        return null;
    }

    public synchronized void put() throws InterruptedException {
        while (getSize() >= 10) {
            wait();
        }

        Student student = Student.getRandomStudent();
        queue.get(student.subject).add(student);

        System.out.println("[student] Пришел новый студент " + student.toString());
        System.out.println("[info] В очереди теперь " + getSize());
        if (Student.counter > numOfStudents) {
            end = true;
        }
        notify();
    }

    public synchronized void printQueue() {
        for (var stud : queue.values()) {
            for (var i : stud) {
                System.out.println("[queue] " + i.toString());
            }
        }
    }
}