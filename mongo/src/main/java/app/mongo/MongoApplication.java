package app.mongo;

import app.mongo.student.StudentService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MongoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MongoApplication.class, args);

        StudentService studentService = context.getBean(StudentService.class);

        testStudentRepository(studentService);
    }

    public static void testStudentRepository(StudentService service) {
//        service.deleteAllStudents();
//        service.insertAllStudents();
//        service.findByAcademicTitle("RNDr.");
//        service.dropStudyProgramIndexIfExists();
//        service.findStudentsInYearAndProgram(1996, "B");
//        service.createStudyProgramIndex();
//        service.findStudentsInYearAndProgram(1996, "B");
        service.printStudentCountsByYearAndProgram();
    }
}