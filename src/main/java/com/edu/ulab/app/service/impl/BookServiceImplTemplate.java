package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundForUpdateException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());  
        return bookDto;
    }

    @Override
    public List<Long> getListBookIdsByUserId(Long userId) {
        final String SELECT_BOOK_SQL = "SELECT ID FROM BOOK WHERE USER_ID = ?";
        List<Long> booksId = jdbcTemplate.queryForList(SELECT_BOOK_SQL, Long.class, userId);
        log.info("found books id for userId: {}, {}", userId, booksId);
       return booksId;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_BOOK_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? WHERE ID = ?";
        int row = jdbcTemplate.update(UPDATE_BOOK_SQL, bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount(), bookDto.getId());
        if (row == 0) {
            log.info("book for update with id {} not found", bookDto.getId());
            throw new NotFoundForUpdateException(String.format("book for update with id %s not found", bookDto.getId()));
        }
        return getBookById(bookDto.getId());
    }

    @Override
    public BookDto getBookById(Long id) {
        final String SELECT_BOOK_SQL = "SELECT * FROM BOOK WHERE ID = ?";
        try {
            Book book = jdbcTemplate.queryForObject(SELECT_BOOK_SQL, new BookRowMapper(), id);
            log.info("found book: {}", book);
            return bookMapper.bookToBookDto(book);
        }
        catch (EmptyResultDataAccessException e) {
            log.info("book with id: {} not found", id);
            return null;
        }
    }

    @Override
    public void deleteBookById(Long id) {
        final String DELETE_BOOK_SQL = "DELETE FROM BOOK WHERE ID = ?";
        log.info("Deleting existing book with id: {}", id);
        jdbcTemplate.update(DELETE_BOOK_SQL, id);
    }

    @Override
    public List<BookDto> getAllBooks() {
       return null;
    }
}
