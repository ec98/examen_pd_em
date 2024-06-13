package com.distribuida.services;


import com.distribuida.database.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

@ApplicationScoped
public class BookServiceImpl implements BookService {

    @Inject
    EntityManager em;

    @Override
    public Book findByIdM(Integer id) {
        return em.find(Book.class, id);
    }

    @Override
    public List<Book> getAllM() {
        return em.createQuery("select b from Book b order by b.id asc", Book.class).getResultList();
    }

    @Override
    public void createM(Book libro) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(libro);
        tx.commit();
    }

    @Override
    public void updateM(Book libro) {
        em.merge(libro);
    }

    @Override
    public void deleteM(int id) {
        Book book = em.find(Book.class, id);
        em.remove(book);
    }
}
