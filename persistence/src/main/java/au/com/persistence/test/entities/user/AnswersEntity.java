package au.com.persistence.test.entities.user;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ANSWERS_POC")
@NamedQueries({
        @NamedQuery(name = "answer.findById", query = "SELECT u FROM AnswersEntity u WHERE u.id = :id"),
        @NamedQuery(name = "answer.findByAll", query = "SELECT u FROM AnswersEntity u"),
        @NamedQuery(name = "answer.searchAnswer", query = "SELECT u FROM AnswersEntity u WHERE u.userDetailsEntity.id = :userId AND lower(u.answer) like lower(concat('%', :answer, '%'))"),
        @NamedQuery(name = "answer.searchByQuestion", query = "SELECT u FROM AnswersEntity u WHERE u.userDetailsEntity.id = :userId AND u.questionsEntity.id = :questionId"),
        @NamedQuery(name = "answer.findByUserId", query = "SELECT u FROM AnswersEntity u WHERE u.userDetailsEntity.id = :userId")
})
@SequenceGenerator(sequenceName = "ANSWERS_POC_SEQ", allocationSize = 1, name = "ANSWERS_SEQ")
public class AnswersEntity implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "ANSWERS_SEQ")
    @Column(name = "ID", nullable = false)
    private int id;

    @JoinColumn(name = "USER_DETAILS_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    private UserDetailsEntity userDetailsEntity;

    @JoinColumn(name = "QUESTION_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    private QuestionsEntity questionsEntity;

    @Basic
    @Column(name = "ANSWER", nullable = true, length = 200)
    private String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDetailsEntity getUserDetailsEntity() {
        return userDetailsEntity;
    }

    public void setUserDetailsEntity(UserDetailsEntity userDetailsEntity) {
        this.userDetailsEntity = userDetailsEntity;
    }

    public QuestionsEntity getQuestionsEntity() {
        return questionsEntity;
    }

    public void setQuestionsEntity(QuestionsEntity questionsEntity) {
        this.questionsEntity = questionsEntity;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswersEntity that = (AnswersEntity) o;
        return Objects.equals(userDetailsEntity, that.userDetailsEntity) &&
                Objects.equals(questionsEntity, that.questionsEntity) &&
                Objects.equals(answer, that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userDetailsEntity, questionsEntity, answer);
    }
}
