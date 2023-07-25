package com.asvnn.springmongodb.serivce;

import com.asvnn.springmongodb.collection.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {

    String save(Person person);

    List<Person> getPersonStartWith(String name);

    void delete(String id);

    List<Person> getPersonAgeBetween(int minAge, int maxAge);

    Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);
}
