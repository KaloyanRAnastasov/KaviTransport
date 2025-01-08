package com.kavi.transport.repository;

import com.kavi.transport.entity.DrivingLicenseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrivingLicenseTypeRepository extends JpaRepository<DrivingLicenseType, Long> {
}