package com.asvnn.springmongodb.serivce;

import com.asvnn.springmongodb.collection.Person;
import com.asvnn.springmongodb.repository.PersonRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements  PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public String save(Person person) {
        return personRepository.save(person).getPersonId();
    }
    @HystrixCommand(fallbackMethod = "defaultFallBackMethod" , commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    @Override
    public List<Person> getPersonStartWith(String name) {
        return personRepository.findByFirstNameStartsWith(name);
    }

    @Override
    public void delete(String id) {
        if(personRepository.existsById(id)) {
            personRepository.deleteById(id);
        }
    }
    @HystrixCommand(fallbackMethod = "defaultFallBackMethod" , commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    @Override
    public List<Person> getPersonAgeBetween(int minAge, int maxAge) {
        return personRepository.findByPersonAgeBetween(minAge, maxAge);
    }


    @HystrixCommand(fallbackMethod = "defaultFallBackMethod" , commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        if(name!=null && !name.isEmpty()) criteriaList.add(Criteria.where("firstName").regex(name, "i"));

        if(minAge!=null && maxAge!=null) criteriaList.add((Criteria.where("age").gte(minAge).lte(maxAge)));

        if(city!=null && !city.isEmpty()) criteriaList.add(Criteria.where("addresses.city").is(city));

        if(!criteriaList.isEmpty()){

            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class), pageable, () -> mongoTemplate.count(query.skip(0).limit(0), Person.class));

        return people;
    }

    public String defaultFallBackMethod(){
        System.out.println("in the default fallback method");

        return "FALLBAKC RESPONSE";
    }


}
