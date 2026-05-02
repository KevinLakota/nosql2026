package app.mongo.student;

import nosql.aislike.DaoFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final MongoTemplate mongoTemplate;

    public StudentService(StudentRepository studentRepository, MongoTemplate mongoTemplate) {
        this.studentRepository = studentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public MongoStudent save(MongoStudent student) {
        return studentRepository.save(student);
    }

    public Optional<MongoStudent> findById(Long id) {
        return studentRepository.findById(id);
    }

    public Iterable<MongoStudent> findAll() {
        return studentRepository.findAll();
    }

    public long count() {
        return studentRepository.count();
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public void deleteAll() {
        studentRepository.deleteAll();
    }


    public void deleteAllStudents() {
        studentRepository.deleteAll();
        System.out.println("MongoDB kolekcia students bola vymazaná.");
    }

    public void insertAllStudents() {
        List<MongoStudent> students = DaoFactory.INSTANCE
                .getStudentDao()
                .getAll()
                .stream()
                .map(MongoStudent::new)
                .toList();

        System.out.println("Načítaných študentov z MySQL: " + students.size());

        studentRepository.saveAll(students);

        System.out.println("Uložených študentov v MongoDB: " + studentRepository.count());
    }

    public void findByAcademicTitle(String title) {
        System.out.println();
        System.out.println("Akademický titul: " + title);

        List<StudentProjection> students = studentRepository.findBySkratkaakadtitul(title);

        System.out.println("Počet nájdených študentov: " + students.size());

        for (StudentProjection student : students) {
            System.out.println(student.getMeno() + " " + student.getPriezvisko());
        }
        System.out.println("===== KONIEC VÝPISU =====");
    }

    public void findStudentsInYearAndProgram(int rok, String program) {
        long start = System.nanoTime();
        System.out.println();
        System.out.println("===== ŠTUDENTI ŠTUDUJÚCI V DANOM ROKU V PROGRAME =====");

        List<MongoStudent> students = studentRepository.findByStudyProgram(program);

        System.out.println("Rok: " + rok);
        System.out.println("Študijný program: " + program);
        System.out.println("Počet študentov s daným programom pred filtrovaním podľa roka: " + students.size());

        int count = 0;

        for (MongoStudent student : students) {
            boolean studentPrinted = false;

            for (MongoStudium studium : student.getStudium()) {
                if (studium.getStudijnyProgram() == null) {
                    continue;
                }

                boolean sameProgram = program.equals(studium.getStudijnyProgram().getSkratka());
                boolean activeInYear = isYearBetweenStudyDates(
                        rok,
                        studium.getZaciatokStudia(),
                        studium.getKoniecStudia()
                );

                if (sameProgram && activeInYear) {
                    if (!studentPrinted) {
                        System.out.println(student.getId() + " | "
                                + student.getMeno() + " "
                                + student.getPriezvisko());

                        studentPrinted = true;
                        count++;
                    }

                    System.out.println("   štúdium: "
                            + studium.getZaciatokStudia()
                            + " - "
                            + studium.getKoniecStudia()
                            + " | program: "
                            + studium.getStudijnyProgram().getSkratka()
                            + " | "
                            + studium.getStudijnyProgram().getPopis());
                }
            }
        }

        System.out.println("Počet nájdených študentov: " + count);
        long end = System.nanoTime();
        long duration = end - start;
        System.out.println("Čas dopytu: " + (duration / 1_000_000.0) + " ms");
        System.out.println("===== KONIEC VÝPISU =====");
    }

    public void createStudyProgramIndex() {
        mongoTemplate.indexOps(MongoStudent.class)
                .createIndex(new Index()
                        .on("studium.studijnyProgram.skratka", Sort.Direction.ASC)
                        .named("idx_studijny_program"));

    }

    public void dropStudyProgramIndexIfExists() {
        try {
            mongoTemplate.indexOps(MongoStudent.class)
                    .dropIndex("idx_studijny_program");

            System.out.println("Existujúci index idx_studijny_program bol zmazaný.");
        } catch (Exception e) {
            System.out.println("Index idx_studijny_program ešte neexistoval.");
        }
    }

    public void printStudentCountsByYearAndProgram() {
        System.out.println();
        System.out.println("===== POČTY ŠTUDENTOV PODĽA ROKOV A ŠTUDIJNÝCH PROGRAMOV =====");

        Map<String, Integer> counts = new TreeMap<>();

        for (MongoStudent student : studentRepository.findAll()) {
            for (MongoStudium studium : student.getStudium()) {
                if (studium.getStudijnyProgram() == null) {
                    continue;
                }

                Integer rok = extractYear(studium.getZaciatokStudia());

                if (rok == null) {
                    continue;
                }

                String key = rok + "|" + studium.getStudijnyProgram().getSkratka();

                counts.put(key, counts.getOrDefault(key, 0) + 1);
            }
        }

        System.out.printf("%-10s %-25s %-10s%n", "Rok", "Študijný program", "Počet");
        System.out.println("------------------------------------------------");

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String[] parts = entry.getKey().split("\\|");

            String rok = parts[0];
            String program = parts[1];
            Integer count = entry.getValue();

            System.out.printf("%-10s %-25s %-10d%n", rok, program, count);
        }

        System.out.println("===== KONIEC TABUĽKY =====");
    }

    private boolean isYearBetweenStudyDates(int rok, String zaciatokStudia, String koniecStudia) {
        Integer startYear = extractYear(zaciatokStudia);
        Integer endYear = extractYear(koniecStudia);

        if (startYear == null) {
            return false;
        }

        if (endYear == null) {
            return rok >= startYear;
        }

        return rok >= startYear && rok <= endYear;
    }

    private Integer extractYear(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }

        String[] parts = date.trim().split("\\.");

        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}