package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.StreamSupport;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу и автора. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {

        Long id = 2002L;

        //When
        Book result = bookRepository.findById(id).orElse(null);

        //Then
        assertThat(result.getPageCount()).isEqualTo(5500);
        assertThat(result.getTitle()).isEqualTo("default book");
        assertThat(result.getAuthor()).isEqualTo("author");
        assertThat(result.getPerson().getFullName()).isEqualTo("default uer");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Получить книгу и автора. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBooks_thenAssertDmlCount() {

        Iterable<Book> allBooks = bookRepository.findAll();

        //Then
        assertThat(StreamSupport.stream(allBooks.spliterator(), false).count()).isEqualTo(2L);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }



    @DisplayName("Обновить книгу и автора. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        //Given
        //
        Person person = new Person();
        person.setAge(33);
        person.setTitle("reader");
        person.setFullName("Oleg Popov");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setId(2002L);
        book.setAuthor("New Author");
        book.setTitle("New title");
        book.setPageCount(455);

        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(455);
        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getAuthor()).isEqualTo("New Author");
        assertThat(result.getPerson().getFullName()).isEqualTo("Oleg Popov");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {
        //Given
        Book book = new Book();
        book.setId(2002L);

        //When
        bookRepository.delete(book);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


}
