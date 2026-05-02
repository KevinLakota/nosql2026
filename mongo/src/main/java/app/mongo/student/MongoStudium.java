package app.mongo.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nosql.aislike.entity.Studium;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MongoStudium {

    private Long id;
    private String zaciatokStudia;
    private String koniecStudia;
    private MongoStudijnyProgram studijnyProgram;

    public MongoStudium(Studium studium) {
        this.id = studium.getId();
        this.zaciatokStudia = studium.getZaciatokStudia();
        this.koniecStudia = studium.getKoniecStudia();
        this.studijnyProgram = new MongoStudijnyProgram(studium.getStudijnyProgram());
    }
}