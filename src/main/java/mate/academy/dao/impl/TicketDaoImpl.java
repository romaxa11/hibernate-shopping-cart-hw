package mate.academy.dao.impl;

import mate.academy.dao.TicketDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.MovieSession;
import mate.academy.model.Ticket;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Dao
public class TicketDaoImpl implements TicketDao {
    private static final SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public Ticket add(Ticket ticket) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add ticket to DB " + ticket, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return ticket;
    }

    public Ticket get(MovieSession movieSession, User user) {
        try (Session session = factory.openSession()) {
            return session.createQuery("select distinct t from Ticket t "
                            + "left join fetch t.user "
                            + "left join fetch t.movieSession "
                            + "where t.user = :user "
                            + " and t.movieSession = :movieSession", Ticket.class)
                    .setParameter("user", user)
                    .setParameter("movieSession", movieSession)
                    .getSingleResult();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get ticket by user "
                    + user.getEmail() + " and movie session " + movieSession.getMovie(), e);
        }
    }
}