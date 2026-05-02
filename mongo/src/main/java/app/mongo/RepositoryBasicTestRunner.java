package app.mongo;

import app.mongo.student.MongoStudent;
import app.mongo.student.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
//@Component
public class RepositoryBasicTestRunner implements CommandLineRunner {

    private final StudentRepository studentRepository;

    public RepositoryBasicTestRunner(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("===== TEST ZÁKLADNEJ FUNKCIONALITY CRUD REPOSITORY =====");

        System.out.println("Počet študentov v MongoDB: " + studentRepository.count());

        System.out.println();
        System.out.println("Prvých 5 študentov:");

        int counter = 0;
        MongoStudent firstStudent = null;

        for (MongoStudent student : studentRepository.findAll()) {
            if (firstStudent == null) {
                firstStudent = student;
            }

            System.out.println(student.getId() + " | "
                    + student.getMeno() + " "
                    + student.getPriezvisko() + " | titul: "
                    + student.getSkratkaakadtitul());

            counter++;

            if (counter >= 5) {
                break;
            }
        }

        if (firstStudent != null) {
            Long id = firstStudent.getId();

            System.out.println();
            System.out.println("Test findById() pre ID: " + id);

            Optional<MongoStudent> foundStudent = studentRepository.findById(id);

            if (foundStudent.isPresent()) {
                System.out.println("Nájdený študent:");
                System.out.println(foundStudent.get());
            } else {
                System.out.println("Študent sa nenašiel.");
            }

            System.out.println();
            System.out.println("Test existsById(): " + studentRepository.existsById(id));
        }

        System.out.println("===== KONIEC TESTU =====");
    }
}