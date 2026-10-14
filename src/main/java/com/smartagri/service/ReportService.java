package com.smartagri.service;

import com.smartagri.entity.Crop;
import com.smartagri.entity.Expense;
import com.smartagri.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {
    private final CropRepository crops;
    private final ExpenseRepository expenses;
    private final OrderRepository orders;
    private final FarmRepository farms;
    private final ProductRepository products;

    public ReportService(CropRepository crops, ExpenseRepository expenses, OrderRepository orders,
                         FarmRepository farms, ProductRepository products) {
        this.crops = crops; this.expenses = expenses; this.orders = orders;
        this.farms = farms; this.products = products;
    }

    private static double n(Double d) { return d == null ? 0 : d; }

    public Map<String, Object> farmerReport(Long farmerId) {
        List<Crop> cropList = crops.findByFarmOwnerId(farmerId);
        List<Expense> expenseList = expenses.findByCropFarmOwnerId(farmerId);

        Map<String, Double> byCategory = new LinkedHashMap<>();
        Map<Long, Double> expenseByCrop = new HashMap<>();
        double totalExpenses = 0;
        for (Expense e : expenseList) {
            double amt = n(e.getAmount());
            totalExpenses += amt;
            byCategory.merge(e.getCategory() == null ? "OTHER" : e.getCategory(), amt, Double::sum);
            expenseByCrop.merge(e.getCrop().getId(), amt, Double::sum);
        }

        double cropRevenue = 0, totalYield = 0;
        List<Map<String, Object>> perCrop = new ArrayList<>();
        for (Crop c : cropList) {
            double rev = n(c.getRevenue());
            double exp = expenseByCrop.getOrDefault(c.getId(), 0.0);
            cropRevenue += rev;
            totalYield += n(c.getYieldKg());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("crop", c.getName());
            row.put("expenses", exp);
            row.put("revenue", rev);
            row.put("profit", rev - exp);
            perCrop.add(row);
        }

        double marketplaceSales = n(orders.marketplaceSales(farmerId));
        double totalRevenue = cropRevenue + marketplaceSales;

        List<Map<String, Object>> categoryList = new ArrayList<>();
        byCategory.forEach((k, v) -> categoryList.add(Map.of("name", k, "value", v)));

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("farms", farms.findByOwnerId(farmerId).size());
        r.put("crops", cropList.size());
        r.put("products", products.countByFarmerId(farmerId));
        r.put("totalYieldKg", totalYield);
        r.put("totalExpenses", totalExpenses);
        r.put("cropRevenue", cropRevenue);
        r.put("marketplaceSales", marketplaceSales);
        r.put("totalRevenue", totalRevenue);
        r.put("profit", totalRevenue - totalExpenses);
        r.put("expenseByCategory", categoryList);
        r.put("cropSummary", perCrop);
        return r;
    }
}
