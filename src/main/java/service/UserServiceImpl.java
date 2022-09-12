package service;

import model.User;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Repository
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public List<User> listAll() {
        log.debug("findAll: <- ");

        return em.createNamedQuery(User.FIND_ALL, User.class).getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> list(int count) {
        log.debug("find: <- " + count);
        TypedQuery<User> query = em.createNamedQuery(User.FIND_ALL, User.class);

        return query.getResultList().stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        log.debug("add: <- " + user);

        em.persist(user);
        log.debug("add: -> " + user);
        return user;
    }

    public User update(User user) {
        log.debug("update: <- " + user);

        User usr = em.merge(user);
        log.debug("update: -> " + usr);

        return usr;
    }

    @Override
    public void delete(User user) {
        log.debug("delete: <- " + user);

        // User mergedUser = em.merge(user);
        // em.remove(mergedUser);
        // log.debug("delete: -> User with id=" + user.getId() + " deleted successfully");
        // em.remove(mergedUser);
        delete(user.getId());
    }

    @Override
    public void delete(Long id) {
        log.debug("delete: <- " + id);

        // int cnt = em.createQuery("delete from User where id = :id")
        int cnt = em.createNamedQuery(User.DELETE_USER_BY_ID)
                .setParameter("id", id)
                .executeUpdate();
        String status = (cnt == 1) ? "deleted successfully" : "not found";
        log.debug("delete: -> User with id=" + id + " " + status);
    }

    @Override
    public User update(long id, User user) {
        log.debug("update: <- id=" + id + ", user=" + user);
        User usr = find(id);
        if (usr != null) {
            usr.setFirstName(user.getFirstName());
            usr.setSecondName(user.getSecondName());
            usr.setAge(user.getAge());
            return update(usr);
        } else {
            log.warn("update: User with id=" + id + " not found");
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public User find(Long id) {
        User usr;

        log.debug("find: <- " + id);

        //User usr = em.find(User.class, id);
        List<User> usrs = em.createNamedQuery(User.FIND_USER_BY_ID, User.class)
                .setParameter("id", id)
                .getResultList();
        usr = usrs.isEmpty() ? null : usrs.get(0);
        log.debug("find: -> " + usr);
        return usr;
    }
}
