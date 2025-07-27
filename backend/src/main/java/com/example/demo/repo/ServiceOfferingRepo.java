package com.example.demo.repo;

import com.example.demo.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceOfferingRepo extends JpaRepository<ServiceOffering, Long> {

}
