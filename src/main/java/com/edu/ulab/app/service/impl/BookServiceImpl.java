package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundForUpdateException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public List<Long> getListBookIdsByUserId(Long userId) {
        log.info("get all books for user id: {}", userId);
        Iterable<Book> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false)
                .filter(book -> userId.equals(book.getPerson().getId()))
                .peek(book -> log.info("book for userId: {}, {}", userId, book))
                .map(Book::getId)
                .peek(bookId -> log.info("bookId for userId: {}, {}", userId, bookId))
                .toList();
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (!bookRepository.existsById(bookDto.getId())) {
            log.info("book for update with id {} not found", bookDto.getId());
            throw new NotFoundForUpdateException(String.format("book for update with id %s not found", bookDto.getId()));
        }
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book updatedBook = bookRepository.save(book);
        log.info("Saved book: {}", updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        log.info("found book with id: {}, {}", id, book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        log.info("deleting book with id: {}", id);
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("get all books:");
        Iterable<Book> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false)
                .map(bookMapper::bookToBookDto)
                .collect(Collectors.toList());
    }
}
