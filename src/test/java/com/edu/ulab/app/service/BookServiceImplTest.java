package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setPageCount(1000);
        updatedBook.setTitle("test title");
        updatedBook.setAuthor("test author");
        updatedBook.setPerson(person);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        //when

        when(bookRepository.existsById(bookDto.getId())).thenReturn(true);
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1L, bookDtoResult.getUserId());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals("test title", bookDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение книги по id. Должно пройти успешно.")
    void getBook_Test() {
        //given
        Person person  = new Person();
        person.setId(1L);

        Long id = 33L;

        Book book = new Book();
        book.setId(33L);
        book.setPageCount(434);
        book.setTitle("Art");
        book.setAuthor("no author");
        book.setPerson(person);

        BookDto result = new BookDto();
        result.setId(33L);
        result.setUserId(1L);
        result.setAuthor("no author");
        result.setTitle("Art");
        result.setPageCount(434);

        //when

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookService.getBookById(id);
        assertEquals(33L, bookDtoResult.getId());
        assertEquals(1L, bookDtoResult.getUserId());
        assertEquals("no author", bookDtoResult.getAuthor());
        assertEquals("Art", bookDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение всех книг. Должно пройти успешно.")
    void getAllBooks_Test() {
        //given
        Person person1  = new Person();
        person1.setId(1L);

        Person person2  = new Person();
        person2.setId(2L);

        Book book1 = new Book(3L, "Title 1", "Author1", 222, person1);
        Book book2 = new Book(4L, "Title 2", "Author2", 368, person1);
        Book book3 = new Book(5L, "Title 3", "Author3", 189, person2);
        Book book4 = new Book(6L, "Title 4", "Author4", 256, person2);

        BookDto bookDto1 = new BookDto(3L, person1.getId(), "Title 1", "Author1", 222);
        BookDto bookDto2 = new BookDto(4L, person1.getId(), "Title 2", "Author2", 368);
        BookDto bookDto3 = new BookDto(5L, person2.getId(), "Title 3", "Author3", 189);
        BookDto bookDto4 = new BookDto(6L, person2.getId(), "Title 4", "Author4", 256);

        //when

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4));
        when(bookMapper.bookToBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(book2)).thenReturn(bookDto2);
        when(bookMapper.bookToBookDto(book3)).thenReturn(bookDto3);
        when(bookMapper.bookToBookDto(book4)).thenReturn(bookDto4);

        //then

        List<BookDto> allBooks = bookService.getAllBooks();
        assertNotNull(allBooks);
        assertEquals(4, allBooks.size());
    }

    @Test
    @DisplayName("Удаление книги по id. Должно пройти успешно.")
    void deleteBook_Test() {
        //given

        Long id = 1L;

        //when

        //then

        bookService.deleteBookById(id);
        verify(bookRepository, times(1)).deleteById(id);

    }

}
