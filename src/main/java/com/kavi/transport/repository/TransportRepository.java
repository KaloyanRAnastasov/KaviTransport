package com.kavi.transport.repository;

import com.kavi.transport.entity.Transport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface TransportRepository extends JpaRepository<Transport, Long> {

    Page<Transport> findByClientId(Long clientId, Pageable pageable);

    Page<Transport> findByVehicleId(Long vehicleId, Pageable pageable);

    Page<Transport> findByClientIdAndVehicleId(Long clientId, Long vehicleId, Pageable pageable);

    Page<Transport> findByClientIdAndVehicleIdAndIsPaid(Long clientId, Long vehicleId, Boolean isPaid, Pageable pageable);

    @Query("SELECT t FROM Transport t LEFT JOIN t.payments p WHERE (p.isPaid = :isPaid OR p IS NULL)")
    Page<Transport> findByIsPaid(@Param("isPaid") Boolean isPaid, Pageable pageable);

    @Query("""
    SELECT COALESCE(SUM(t.price), 0)
    FROM Transport t
    LEFT JOIN t.payments p
    WHERE t.client.id = :clientId
    AND (p IS NULL OR p.isPaid = false)
""")
    BigDecimal findUnpaidTransportSumByClientId(@Param("clientId") Long clientId);
}
