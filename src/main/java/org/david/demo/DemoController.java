package org.david.demo;

import org.david.demo.model.Student;
import org.david.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    private StudentService studentService;

    @RequestMapping("/add/{name}/{age}")
    public Student add(@PathVariable String name,@PathVariable int age){
        return studentService.add(name,age);
    }

    @RequestMapping("/test")
    public Boolean testTransaction(){
        return studentService.testTransaction();
    }

    @RequestMapping("/list")
    public List<Student> list(){
        return studentService.list();
    }
}
