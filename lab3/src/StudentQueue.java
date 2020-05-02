import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

class StudentQueue {
    private Map<Subject, Queue<Student>> queue = new LinkedHashMap<>(3);

    private final int maxAmount = 10;
    static class Student {
        int labsCount;
        private Subject subject;

        Student(int labsCount, Subject subject) {
            this.labsCount = labsCount;
            this.subject = subject;
        }

        public int getLabsCount() {
            return labsCount;
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

    StudentQueue() {
        for (Subject subject : Subject.values()) {
            queue.put(subject, new ConcurrentLinkedQueue<>());
        }
    }
    private int getSize()
    {
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

    public synchronized void put() throws InterruptedException{
        while (getSize() >= maxAmount) {
            wait();
        }
        Student student = Student.getRandomStudent();
        queue.get(student.subject).add(student);

        System.out.println("[student] Пришел новый студент " + student.toString());
        System.out.println("[info] В очереди теперь " + getSize());


        notify();
    }

    public synchronized void printQueue() {
        for (var stud :  queue.values()) {
            for(var i : stud)
            {
                System.out.println("[queue] " + i.toString());
            }
        }
    }
}