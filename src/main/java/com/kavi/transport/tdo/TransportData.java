package com.kavi.transport.tdo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String startPoint;
    private String endPoint;
    private String departureDate;
    private String arrivalDate;
    private BigDecimal price;
    private String client;
    private String driver;
    private String vehicle;
}
