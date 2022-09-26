package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundForUpdateException;
import com.edu.ulab.app.mapper.PersonRowMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        int row = jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userDto.getId());
        if (row == 0) {
            log.info("user for update with id {} not found", userDto.getId());
            throw new NotFoundForUpdateException(String.format("user for update with id %s not found", userDto.getId()));
        }
        return getUserById(userDto.getId());
    }

    @Override
    public UserDto getUserById(Long id) {

        final String SELECT_SQL = "SELECT * FROM PERSON WHERE ID = ?";
        try {
            Person user = jdbcTemplate.queryForObject(SELECT_SQL,new PersonRowMapper(), id);
            log.info("found user: {}", user);
            return userMapper.personToUserDto(user);
        }
        catch (EmptyResultDataAccessException e) {
            log.info("user with id: {} not found", id);
            return null;
        }
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ?";
        log.info("Deleting existing user with id: {}", id);
        jdbcTemplate.update(DELETE_SQL, id);
    }
}
