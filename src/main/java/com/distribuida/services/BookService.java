package com.distribuida.services;

import com.distribuida.database.Book;

import java.util.List;

public interface BookService {

    //Find-Id
    public Book findByIdM(Integer id);

    //C-R-U-D
    public List<Book> getAllM();

    public void createM(Book libro);

    public void updateM(Book libro);

    public void deleteM(int id);
}
