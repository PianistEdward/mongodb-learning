package org.david.demo.service;

import org.david.demo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Student add(String name,int age){
        Student student1 = new Student(name,age);
        mongoTemplate.save(student1);
        return student1;
    }

    @Override
    public List<Student> list(){
        return mongoTemplate.findAll(Student.class);
    }

    @Override
    public Boolean testTransaction(){
        return transactionTemplate.execute(e -> {
            add("1", 1);
            add("2", 2);
            add("3", 3);
            add("3",3);
            return true;
        });
    }
}
