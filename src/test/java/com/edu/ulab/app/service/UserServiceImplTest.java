package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
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
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());

    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1001L);
        userDto.setAge(19);
        userDto.setFullName("Ivan");
        userDto.setTitle("new title");

        Person person  = new Person();
        person.setId(1001L);
        person.setAge(19);
        person.setFullName("Ivan");
        person.setTitle("new title");

        Person updatedPerson  = new Person();
        updatedPerson.setId(1001L);
        updatedPerson.setAge(19);
        updatedPerson.setFullName("Ivan");
        updatedPerson.setTitle("new title");

        UserDto result = new UserDto();
        result.setId(1001L);
        result.setAge(19);
        result.setFullName("Ivan");
        result.setTitle("new title");

        //when

        when(userRepository.existsById(userDto.getId())).thenReturn(true);
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1001L, userDtoResult.getId());
        assertEquals("Ivan", userDtoResult.getFullName());
        assertEquals("new title", userDtoResult.getTitle());
        assertEquals(19, userDtoResult.getAge());
    }

    @Test
    @DisplayName("Получение пользователя по id. Должно пройти успешно.")
    void getPerson_Test() {
        //given

        Long id = 1001L;

        Person person  = new Person();
        person.setId(1001L);
        person.setAge(55);
        person.setFullName("default uer");
        person.setTitle("reader");

        UserDto result = new UserDto();
        result.setId(1001L);
        result.setAge(55);
        result.setFullName("default uer");
        result.setTitle("reader");

        //when

        when(userRepository.findById(id)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(result);

        //then

        UserDto userDtoGiven = userService.getUserById(id);
        assertEquals(1001L, userDtoGiven.getId());
        assertEquals("default uer", userDtoGiven.getFullName());
        assertEquals("reader", userDtoGiven.getTitle());
        assertEquals(55, userDtoGiven.getAge());
    }

    @Test
    @DisplayName("Получение всех пользователей. Должно пройти успешно.")
    void getAllPersons_Test() {
        //given

        Person person1  = new Person();
        person1.setId(101L);
        person1.setAge(25);
        person1.setFullName("Igor");
        person1.setTitle("reader");

        Person person2  = new Person();
        person2.setId(102L);
        person2.setAge(28);
        person2.setFullName("Anna");
        person2.setTitle("reader");

        UserDto userDto1  = new UserDto();
        userDto1.setId(101L);
        userDto1.setAge(25);
        userDto1.setFullName("Igor");
        userDto1.setTitle("reader");

        UserDto userDto2  = new UserDto();
        userDto2.setId(102L);
        userDto2.setAge(28);
        userDto2.setFullName("Anna");
        userDto2.setTitle("reader");

        //when

        when(userRepository.findAll()).thenReturn(List.of(person1, person2));
        when(userMapper.personToUserDto(person1)).thenReturn(userDto1);
        when(userMapper.personToUserDto(person2)).thenReturn(userDto2);

        //then

        List<UserDto> allUsers = userService.getAllUsers();
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Удаление пользователя по id. Должно пройти успешно.")
    void deletePerson_Test() {
        //given

        Long id = 1001L;

        //when

        //then

        userService.deleteUserById(id);
        verify(userRepository, times(1)).deleteById(id);

    }

}
