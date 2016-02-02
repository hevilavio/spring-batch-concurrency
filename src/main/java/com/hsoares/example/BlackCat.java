package com.hsoares.example;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by hevilavio on 2/2/16.
 */
@Component
@EnableAsync
@EnableScheduling
public class BlackCat {

    @Autowired
    DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Scheduled(fixedDelay = 5000)
    public void doStuff(){
        System.out.println("\n\nSTART\n\n");

        Integer result = jdbcTemplate.query("SELECT 1 FROM DUAL", resultSet -> {

            resultSet.next();
            return resultSet.getInt(1);
        });
        System.out.println("\n\nRESULT = " + result + "\n\n");
    }
}
