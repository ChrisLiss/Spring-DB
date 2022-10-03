package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
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
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число select должно равняться 1") //todo
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(29);
        person.setTitle("reader_new");
        person.setFullName("Alex Kruglov");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(29);
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getFullName()).isEqualTo("Alex Kruglov");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);

        //When
        userRepository.delete(person);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {
        //Given
        Long id = 1001L;

        //When
        Person givenUser = userRepository.findById(id).orElse(null);

        //Then
        assertThat(givenUser.getAge()).isEqualTo(55);
        assertThat(givenUser.getTitle()).isEqualTo("reader");
        assertThat(givenUser.getFullName()).isEqualTo("default uer");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить всех пользователей. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllPerson_thenAssertDmlCount() {
        //Given

        //When
        Iterable<Person> allPerson = userRepository.findAll();

        //Then
        assertThat(StreamSupport.stream(allPerson.spliterator(), false).count()).isEqualTo(1L);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


}
