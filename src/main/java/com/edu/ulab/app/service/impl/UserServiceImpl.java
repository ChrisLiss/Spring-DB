package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundForUpdateException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (!userRepository.existsById(userDto.getId())) {
            log.info("user for update with id {} not found", userDto.getId());
            throw new NotFoundForUpdateException(String.format("user for update with id %s not found", userDto.getId()));
        }
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person updatedUser = userRepository.save(user);
        log.info("Saved user: {}", updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        Person user = userRepository.findById(id).orElse(null);
        log.info("founded user: {}", user);
        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("deleting user with id: {}", id);
        userRepository.deleteById(id);
    }
}
