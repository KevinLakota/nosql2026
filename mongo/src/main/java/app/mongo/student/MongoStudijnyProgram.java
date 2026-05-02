package app.mongo.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nosql.aislike.entity.StudijnyProgram;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MongoStudijnyProgram {

    private Long id;
    private String skratka;
    private String popis;

    public MongoStudijnyProgram(StudijnyProgram program) {
        this.id = program.getId();
        this.skratka = program.getSkratka();
        this.popis = program.getPopis();
    }
}