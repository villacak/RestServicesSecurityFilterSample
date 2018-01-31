package au.com.persistence.test.entities.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "QUESTION_POC")
@NamedQueries({
        @NamedQuery(name = "question.findById", query = "SELECT u FROM QuestionsEntity u WHERE u.id = :id"),
        @NamedQuery(name = "question.findByAll", query = "SELECT u FROM QuestionsEntity u"),
        @NamedQuery(name = "question.searchQuestion", query = "SELECT u FROM QuestionsEntity u WHERE lower(u.question) like lower(concat('%', :question, '%'))")
})
@SequenceGenerator(sequenceName = "QUESTIONS_POC_SEQ", allocationSize = 1, name = "QUESTIONS_SEQ")
public class QuestionsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "QUESTIONS_SEQ")
    @Column(name = "ID", nullable = false)
    private int id;

    @Basic
    @Column(name = "QUESTION", nullable = true, length = 150)
    private String question;

    @Basic
    @Column(name = "IS_ACTIVE")
    private int active;

    private transient boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isActive() {
        final boolean activeToReturn = (active == 1)? true: false;
        return activeToReturn;
    }

    public void setActive(final boolean isActive) {
        this.isActive = isActive;
        active = (isActive)? 1: 0;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final QuestionsEntity that = (QuestionsEntity) o;

        if (id != that.id) return false;
        if (question != null ? !question.equals(that.question) : that.question != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (question != null ? question.hashCode() : 0);
        return result;
    }
}
