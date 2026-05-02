package app.mongo.student;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<MongoStudent, Long> {

    //List<MongoStudent> findBySkratkaakadtitul(String skratkaakadtitul);

    List<StudentProjection> findBySkratkaakadtitul(String skratkaakadtitul);

    @Query("{ 'studium.studijnyProgram.skratka': ?0 }")
    List<MongoStudent> findByStudyProgram(String program);

}