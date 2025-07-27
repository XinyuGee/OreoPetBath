package com.example.demo.repo;

import com.example.demo.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepo extends JpaRepository<Service, Long> {

}
