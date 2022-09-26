package com.edu.ulab.app.mapper;

import com.edu.ulab.app.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person user = new Person();
        user.setId(rs.getLong("ID"));
        user.setFullName(rs.getString("FULL_NAME"));
        user.setTitle(rs.getString("TITLE"));
        user.setAge(rs.getInt("AGE"));
        return user;
    }

}
