package com.kafka.connect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.ParametersEntity;
import com.kafka.connect.repository.ParameterRepository;

@RestController
@RequestMapping("/parameters")
public class ParameterController
{

    @Autowired
    private ParameterRepository repository;

    @GetMapping
    public List<ParametersEntity> getAllConnectors()
    {
        return repository.findAll();
    }

}
