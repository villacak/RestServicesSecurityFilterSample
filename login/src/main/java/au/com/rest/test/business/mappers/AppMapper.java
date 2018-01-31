package au.com.rest.test.business.mappers;


import au.com.persistence.test.dao.PersistenceDAO;
import au.com.persistence.test.entities.user.AnswersEntity;
import au.com.persistence.test.entities.user.QuestionsEntity;
import au.com.persistence.test.entities.user.UserDetailsEntity;
import au.com.persistence.test.entities.user.UserEntity;
import au.com.rest.test.pojos.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Once both modules became projects this mapper packege is not needed anymore
 * as the map will be straight from JSON to pojos and if validation succeed or any other needed task
 * to be done.
 * Then is just call the service.
 */
public class AppMapper {

    public UserEntity toUserEntity(final UserLogin payload) {
        final UserEntity toReturn = new UserEntity();
        toReturn.setId(payload.getId());
        toReturn.setLogin(payload.getLogin());
        toReturn.setPassword(payload.getPassword());
        return toReturn;
    }


    public UserLogin toUser(final UserEntity entity) {
        final UserLogin toReturn = new UserLogin();
        toReturn.setId(entity.getId());
        toReturn.setLogin(entity.getLogin());
        toReturn.setPassword(entity.getPassword());
        return toReturn;
    }


    public UserDetailsEntity toUserDetailsToEntity(final UserAccountDetails payload) {
        final UserEntity innerToReturn = toUserEntity(payload.getUserLogin());
        final UserDetailsEntity toReturn = new UserDetailsEntity();
        toReturn.setId(payload.getId());
        toReturn.setEmail(payload.getEmail());
        toReturn.setFullName(payload.getFullName());
        toReturn.setPhone(payload.getPhone());
        toReturn.setUserEntity(innerToReturn);
        return toReturn;
    }


    public UserAccountDetails toUserDetails(final UserDetailsEntity entity, final boolean isWithAnswer) {
        final UserLogin userLogin = toUser(entity.getUserEntity());
        final PersistenceDAO dao = new PersistenceDAO();
        final List<AnswersEntity> answersEntityList = dao.getAnswersByUserId(userLogin.getId());
        final List<Answer> answers = toAnswers(answersEntityList, isWithAnswer);
        final UserAccountDetails toReturn = new UserAccountDetails();
        toReturn.setId(userLogin.getId());
        toReturn.setEmail(entity.getEmail());
        toReturn.setFullName(entity.getFullName());
        toReturn.setPhone(entity.getPhone());
        toReturn.setUserLogin(userLogin);
        toReturn.setAnswerList(answers);
        return toReturn;
    }



    public List<Question> toQuestions(final List<QuestionsEntity> entityList) {
        final List<Question> questionList = new ArrayList<>(entityList.size());
        for(QuestionsEntity entity: entityList) {
            final Question tempQuestion = toQuestion(entity);
            questionList.add(tempQuestion);
        }
        return questionList;
    }



    public Question toQuestion(final QuestionsEntity entity) {
        final Question question = new Question();
        question.setId(entity.getId());
        question.setQuestion(entity.getQuestion());
        question.setActive(entity.isActive());
        return question;
    }


    public QuestionsEntity toQuestionsEntity(final Question payload) {
        final QuestionsEntity entity = new QuestionsEntity();
        entity.setId(payload.getId());
        entity.setQuestion(payload.getQuestion());
        entity.setActive(payload.isActive());
        return entity;
    }



    public List<Answer> toAnswers(final List<AnswersEntity> entityList, final boolean isWithAnswer) {
        final List<Answer> answersList = new ArrayList<>(entityList.size());
        for(AnswersEntity entity: entityList) {
            final Answer tempAnswer;
            if (isWithAnswer) {
                tempAnswer = toAnswer(entity);
            } else {
                tempAnswer = toAnswerWithoutIt(entity);
            }
            answersList.add(tempAnswer);
        }
        return answersList;
    }


    public Answer toAnswer(final AnswersEntity entity) {
        final Answer answer = new Answer();
        final Question question = toQuestion(entity.getQuestionsEntity());
        answer.setId(entity.getId());
        answer.setQuestion(question);
        answer.setAnswer(entity.getAnswer());
        return answer;
    }


    public Answer toAnswerWithoutIt(final AnswersEntity entity) {
        final Answer answer = new Answer();
        final Question question = toQuestion(entity.getQuestionsEntity());
        answer.setId(entity.getId());
        answer.setQuestion(question);
        answer.setAnswer(null);
        return answer;
    }


    public List<AnswersEntity> toAnswersEntity(final List<Answer> payload) {
        final List<AnswersEntity> answersEntityList = new ArrayList<>(payload.size());
        for (Answer tempAnser: payload) {
            final AnswersEntity entity = toAnswerEntity(tempAnser);
            answersEntityList.add(entity);
        }
        return answersEntityList;
    }


    public AnswersEntity toAnswerEntity(final Answer payload) {
        final AnswersEntity entityToReturn = new AnswersEntity();
        final QuestionsEntity questionsEntity = toQuestionsEntity(payload.getQuestion());
        entityToReturn.setAnswer(payload.getAnswer());
        entityToReturn.setQuestionsEntity(questionsEntity);
        entityToReturn.setId(payload.getId());
        return entityToReturn;
    }

}
