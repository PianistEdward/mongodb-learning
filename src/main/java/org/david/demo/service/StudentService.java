package org.david.demo.service;

import org.david.demo.model.Student;

import java.util.List;

public interface StudentService {
    Student add(String name, int age);
    List<Student> list();
    Boolean testTransaction();
}
