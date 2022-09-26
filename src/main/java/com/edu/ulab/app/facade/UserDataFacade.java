package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    @Transactional
    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    @Transactional
    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest, Long userId) {
        log.info("Update user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user request: {}", userDto);
        UserDto userDtoAfterUpdate = userService.updateUser(userDto);
        deleteAllBooksByUserId(userId);
        List<Long> newBookIdList = createBooksAndReturnIds(userBookRequest, userId);
        log.info("Collected new book ids: {}", newBookIdList);

        return UserBookResponse.builder()
                .userId(userDtoAfterUpdate.getId())
                .booksIdList(newBookIdList)
                .build();
    }

    private List<Long> createBooksAndReturnIds(UserBookRequest userBookRequest, Long userId) {
        log.info("create books for userId: {}", userId);
        return  userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userId))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Search user by id: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("found user: {}", userDto);

        if (userDto == null) {
            log.info("user with idUser: {} не найден", userId);
            return null;
        }

        log.info("Search books for userId: {}", userId);
        List<Long> bookIdList = bookService.getListBookIdsByUserId(userId);
        log.info("Collected found book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookIdList)
                .build();

    }

    @Transactional
    public void deleteUserWithBooks(Long userId) {
        deleteAllBooksByUserId(userId);
        log.info("delete user with idUser: {}", userId);
        userService.deleteUserById(userId);
    }

    private void deleteAllBooksByUserId(Long userId) {
        List<Long> bookIdList = bookService.getListBookIdsByUserId(userId);
        if (bookIdList == null) {
            log.info("user with id: {} has no books", userId);
            return;
        }
        log.info("delete all books for userId: {}, {}", userId, bookIdList);

        bookIdList.stream()
                .filter(Objects::nonNull)
                .peek(bookId -> log.info("delete book with id: {}", bookId))
                .forEach(bookService::deleteBookById);
    }
}
