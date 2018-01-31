package au.com.persistence.test.dao;


import au.com.persistence.test.entities.user.*;
import au.com.persistence.test.enums.KeyValueForSearch;

import javax.persistence.*;
import java.util.List;

public class PersistenceDAO {

    private final String PU = "NewPersistenceUnit";

    private EntityManagerFactory emf;
    private EntityManager em;


    /**
     * Create the Entity Manager from the Persistence Unit
     */
    public PersistenceDAO() {
        emf = Persistence.createEntityManagerFactory(PU);
        em = emf.createEntityManager();
    }


    /**
     * Retrive UserEntity
     *
     * @param login
     * @return
     */
    public UserEntity getUser(final String login) {
        final Query query = em.createNamedQuery("user.findByLogin");
        query.setParameter("login", login);
        UserEntity toReturn;
        try {
            toReturn = (UserEntity) query.getSingleResult();
        } catch (Exception nre) {
            toReturn = null;
        }
        return toReturn;
    }


    /**
     * Retrieve user details
     *
     * @param keyValue
     * @param keyType
     * @return
     */
    public UserDetailsEntity getUserDetails(final String keyValue, final KeyValueForSearch keyType) {
        final String namedQuery;
        if (keyType.equals(KeyValueForSearch.EMAIL)) {
            namedQuery = "userDetails.findByEmail";
        } else if (keyType.equals(KeyValueForSearch.FULL_NAME)) {
            namedQuery = "userDetails.findByName";
        } else if (keyType.equals(KeyValueForSearch.LOGIN)) {
            namedQuery = "userDetails.findByLogin";
        } else if (keyType.equals(KeyValueForSearch.EMAIL_TOKEN)) {
            namedQuery = "userDetails.findByToken";
        } else {
            namedQuery = null;
        }

        UserDetailsEntity toReturn;
        if (namedQuery == null) {
            toReturn = null;
        } else {
            final Query query = em.createNamedQuery(namedQuery);
            query.setParameter(keyType.getType(), keyValue);
            try {
                toReturn = (UserDetailsEntity) query.getSingleResult();
            } catch (NoResultException nre) {
                toReturn = null;
            }
        }
        return toReturn;
    }


    /**
     * Return user security data
     *
     * @param keyValue
     * @param keyType
     * @return
     */
    public UserSecurityEntity getUserSecurity(final String keyValue, final KeyValueForSearch keyType) {
        final String namedQuery;
        if (keyType.equals(KeyValueForSearch.LOGIN)) {
            namedQuery = "userSecurity.findByLogin";
        } else if (keyType.equals(KeyValueForSearch.ACCOUNT_STATE)) {
            namedQuery = "userSecurity.findByAccountState";
        } else {
            namedQuery = null;
        }

        final UserSecurityEntity toReturn;
        if (namedQuery == null) {
            toReturn = null;
        } else {
            final Query query = em.createNamedQuery(namedQuery);
            query.setParameter(keyType.getType(), keyValue);
            toReturn = (UserSecurityEntity) query.getSingleResult();
        }
        return toReturn;
    }


    /**
     * Retrieve questions
     *
     * @param keyValue
     * @param keyType
     * @return
     */
    public List<QuestionsEntity> getQuestions(final String keyValue, final KeyValueForSearch keyType) {
        final String namedQuery;
        if (keyType.equals(KeyValueForSearch.ID) && keyValue != null) {
            namedQuery = "question.findById";
        } else if (keyType.equals(KeyValueForSearch.QUESTION) && keyValue != null) {
            namedQuery = "question.searchQuestion";
        } else if (keyType.equals(KeyValueForSearch.QUESTION) && keyValue == null) {
            namedQuery = "question.findByAll";
        } else {
            namedQuery = null;
        }

        final List<QuestionsEntity> toReturn;
        if (namedQuery == null) {
            toReturn = null;
        } else {
            final Query query = em.createNamedQuery(namedQuery);
            if (keyValue != null) {
                query.setParameter(keyType.getType(), keyValue);
            }
            toReturn = query.getResultList();
        }
        return toReturn;
    }


    /**
     * Retrieve answers
     *
     * @param keyValue
     * @param keyType
     * @param userId
     * @return
     */
    public List<AnswersEntity> getAnswers(final String keyValue, final KeyValueForSearch keyType, final int userId) {
        final String USER_ID = "userId";
        final String namedQuery;
        if (keyType.equals(KeyValueForSearch.ID) && keyValue != null && userId == 0) {
            namedQuery = "answer.findById";
        } else if (keyType.equals(KeyValueForSearch.QUESTION) && keyValue != null && userId > 0) {
            namedQuery = "searchByQuestion";
        } else if (keyType.equals(KeyValueForSearch.ANSWER) && keyValue == null && userId == 0) {
            namedQuery = "answer.findByAll";
        } else if (keyType.equals(KeyValueForSearch.ANSWER) && keyValue != null && userId > 0) {
            namedQuery = "answer.searchAnswer";
        } else {
            namedQuery = null;
        }

        final List<AnswersEntity> toReturn;
        if (namedQuery == null) {
            toReturn = null;
        } else {
            final Query query = em.createNamedQuery(namedQuery);
            if (keyValue != null && userId == 0) {
                query.setParameter(keyType.getType(), keyValue);
            } else if (keyValue != null && userId > 0) {
                query.setParameter(USER_ID, userId);
                query.setParameter(keyType.getType(), keyValue);
            }
            toReturn = query.getResultList();
        }
        return toReturn;
    }


    /**
     * Retrieve answers by user id, get all answers and questions from a user
     *
     * @param userId
     * @return
     */
    public List<AnswersEntity> getAnswersByUserId(final int userId) {
        final Query query = em.createNamedQuery("answer.findByUserId");
        query.setParameter("userId", userId);
        final List<AnswersEntity> toReturn = query.getResultList();
        return toReturn;
    }



    /**
     * Generic Persistence
     *
     * @param entity
     * @param <T>
     * @return
     */
    public <T extends Object> T saveData(final T entity) {
        em.getTransaction().begin();
        final T returnEntity = em.merge(entity);
        em.getTransaction().commit();
        return returnEntity;
    }


    /**
     * Close Entity manager and factory
     */
    public void closeEntityManager() {
        if (em != null) {
            em.close();
        }

        if (emf != null) {
            emf.close();
        }
    }
}
