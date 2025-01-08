package com.kavi.transport.repository;


import com.kavi.transport.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findTopByOrderByRevenueDesc();
}