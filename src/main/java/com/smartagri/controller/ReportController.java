package com.smartagri.controller;

import com.smartagri.security.CurrentUser;
import com.smartagri.service.ReportService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reports;
    private final CurrentUser me;

    public ReportController(ReportService reports, CurrentUser me) { this.reports = reports; this.me = me; }

    @GetMapping("/farmer")
    public Map<String, Object> farmer() { return reports.farmerReport(me.get().getId()); }
}
