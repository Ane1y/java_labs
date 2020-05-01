public class Main {

    public static void main(String[] args) {
        StudentQueue queue = new StudentQueue();

        Thread mathThread = new Thread(new Robot(queue, Subject.MATH));
        mathThread.start();

        Thread physicsThread = new Thread(new Robot(queue, Subject.PHYSICS));
        physicsThread.start();

        Thread oopThread = new Thread(new Robot(queue, Subject.OOP));
        oopThread.start();

        Thread studentGen = new Thread(new StudentsGenerator(queue));
        studentGen.start();

    }
}
