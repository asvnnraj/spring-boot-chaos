package com.asvnn.springmongodb.controller;

import com.asvnn.springmongodb.collection.Person;
import com.asvnn.springmongodb.serivce.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;


    @PostMapping
    public String save(@RequestBody Person person){
        return personService.save(person);
    }
    @GetMapping
    public List<Person> getPersonStartWith(@RequestParam String name){

        return personService.getPersonStartWith(name);
    }

    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable String id){
        personService.delete(id);
    }

    @GetMapping("/age")
    public List<Person> getPersonAgeBetween(@RequestParam int minAge, int maxAge){

        return personService.getPersonAgeBetween(minAge, maxAge);
    }

    @GetMapping("/search")
    public Page<Person> searchPerson(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) Integer minAge,
                                     @RequestParam(required = false) Integer maxAge,
                                     @RequestParam(required = false) String city,
                                     @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "5") Integer size
                                     ){
        Pageable pageable = PageRequest.of(page, size);

        return personService.search(name, minAge, maxAge, city, pageable);

    }
}
