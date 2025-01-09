package com.kavi.transport.controller;

import com.kavi.transport.entity.Client;
import com.kavi.transport.entity.Transport;
import com.kavi.transport.service.ClientService;
import com.kavi.transport.service.TransportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller

public class TransportReportController {
    private final TransportService transportService;
    private final ClientService clientService;

    public TransportReportController(TransportService transportService, ClientService clientService) {
        this.transportService = transportService;
        this.clientService = clientService;
    }

    @GetMapping("/transports/driver-report")
    public String getDriverReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model
    ) {
        // Assign default values to new local variables
        final LocalDateTime effectiveStartDate = (startDate != null)
                ? startDate
                : LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalDateTime.MIN.toLocalTime());

        final LocalDateTime effectiveEndDate = (endDate != null)
                ? endDate
                : LocalDateTime.of(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), LocalDateTime.MAX.toLocalTime());

        // Filter transports based on the date range
        List<Transport> filteredTransports = transportService.getAllTransports().stream()
                .filter(t -> !t.getDepartureDate().isBefore(effectiveStartDate) && !t.getArrivalDate().isAfter(effectiveEndDate))
                .collect(Collectors.toList());

        // Prepare driver report
        Map<String, Map<String, Object>> driverReport = filteredTransports.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getDriver().getName(),
                        TreeMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), transports -> {
                            BigDecimal totalIncome = transports.stream()
                                    .map(Transport::getPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            int transportCount = transports.size();
                            Map<String, Object> stats = new HashMap<>();
                            stats.put("totalIncome", totalIncome);
                            stats.put("transportCount", transportCount);
                            return stats;
                        })
                ));

        model.addAttribute("driverReport", driverReport);
        model.addAttribute("startDate", effectiveStartDate);
        model.addAttribute("endDate", effectiveEndDate);

        return "driver-report";
    }



    @GetMapping("/transports/report")
    public String getTransportReport(
            @RequestParam(required = false)  LocalDateTime startDate,
            @RequestParam(required = false)  LocalDateTime endDate,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String transportType,
            Model model
    ) {
        List<Transport> allTransports = transportService.getAllTransports();

        // Filtering
        List<Transport> filteredTransports = allTransports.stream()
                .filter(transport -> filterByStartDate(transport, startDate))
                .filter(transport -> filterByEndDate(transport, endDate))
                .filter(transport -> filterByClient(transport, clientId))
                .filter(transport -> filterByTransportType(transport, transportType))
                .collect(Collectors.toList());

        model.addAttribute("transports", filteredTransports);
        model.addAttribute("clients", clientService.getAllClients());

        // Add filter parameters back to the model for form persistence
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("clientId", clientId);
        model.addAttribute("transportType", transportType);

        return "transport-report";
    }


    @GetMapping("/transports/revenue")
    public String getRevenueReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String transportType,
            Model model
    ) {
        // If year is not provided, use current year
        int targetYear = year != null ? year : LocalDate.now().getYear();

        List<Transport> allTransports = transportService.getAllTransports();

        // Filtering by year and transport type
        List<Transport> filteredTransports = allTransports.stream()
                .filter(t -> t.getDepartureDate().getYear() == targetYear)
                .filter(t -> filterByTransportType(t, transportType))
                .collect(Collectors.toList());

        // Initialize a TreeMap with all months (1-12) to ensure all months are represented
        Map<Integer, Map<String, Object>> revenueReport = new TreeMap<>();
        for (int month = 1; month <= 12; month++) {
            revenueReport.put(month, createEmptyMonthStats());
        }

        // Group transports by month and calculate statistics
        Map<Integer, List<Transport>> transportsByMonth = filteredTransports.stream()
                .collect(Collectors.groupingBy(t -> t.getDepartureDate().getMonthValue()));

        // Calculate statistics for each month
        transportsByMonth.forEach((month, transports) -> {
            Map<String, Object> monthStats = calculateMonthStatistics(transports);
            revenueReport.put(month, monthStats);
        });

        // Calculate summary statistics
        Map<String, Object> summaryStats = calculateSummaryStatistics(filteredTransports);

        // Add all necessary attributes to the model
        model.addAttribute("revenueReport", revenueReport);
        model.addAttribute("summaryStats", summaryStats);
        model.addAttribute("year", targetYear);
        model.addAttribute("transportType", transportType);
        model.addAttribute("availableYears", getAvailableYears(allTransports));

        return "revenue-report";
    }

    private Map<String, Object> createEmptyMonthStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", BigDecimal.ZERO);
        stats.put("transportCount", 0);
        stats.put("averageRevenue", BigDecimal.ZERO);
        stats.put("minRevenue", BigDecimal.ZERO);
        stats.put("maxRevenue", BigDecimal.ZERO);
        return stats;
    }

    private Map<String, Object> calculateMonthStatistics(List<Transport> transports) {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal totalRevenue = transports.stream()
                .map(Transport::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int transportCount = transports.size();

        BigDecimal averageRevenue = transportCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(transportCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal minRevenue = transports.stream()
                .map(Transport::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxRevenue = transports.stream()
                .map(Transport::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        stats.put("totalRevenue", totalRevenue);
        stats.put("transportCount", transportCount);
        stats.put("averageRevenue", averageRevenue);
        stats.put("minRevenue", minRevenue);
        stats.put("maxRevenue", maxRevenue);

        return stats;
    }

    private Map<String, Object> calculateSummaryStatistics(List<Transport> transports) {
        Map<String, Object> summary = new HashMap<>();

        BigDecimal totalYearRevenue = transports.stream()
                .map(Transport::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalTransports = transports.size();

        BigDecimal yearlyAverage = totalTransports > 0
                ? totalYearRevenue.divide(BigDecimal.valueOf(totalTransports), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        summary.put("totalYearRevenue", totalYearRevenue);
        summary.put("totalTransports", totalTransports);
        summary.put("yearlyAverage", yearlyAverage);

        return summary;
    }

    private List<Integer> getAvailableYears(List<Transport> transports) {
        return transports.stream()
                .map(t -> t.getDepartureDate().getYear())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean filterByStartDate(Transport transport, LocalDateTime startDate) {
        return startDate == null ||
                !transport.getDepartureDate().isBefore(startDate);
    }

    private boolean filterByEndDate(Transport transport, LocalDateTime endDate) {
        return endDate == null ||
                !transport.getArrivalDate().isAfter(endDate);
    }

    private boolean filterByClient(Transport transport, Long clientId) {
        return clientId == null ||
                (transport.getClient() != null && transport.getClient().getId().equals(clientId));
    }

    private boolean filterByTransportType(Transport transport, String transportType) {
        if (transportType == null) {
            return true;
        }
        return switch (transportType.toLowerCase()) {
            case "cargo" -> transport.getCargoDescription() != null;
            case "passenger" -> transport.getPeopleCount() != null;
            default -> true;
        };
    }
}