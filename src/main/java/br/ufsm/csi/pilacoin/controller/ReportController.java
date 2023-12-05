package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Report;
import br.ufsm.csi.pilacoin.service.ReportService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@CrossOrigin
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/obter")
    public Report getReportStatus() {
        return this.reportService.getReportAtual();
    }

    @GetMapping("/atualizar")
    public String atualizarReportStatus() {
        this.reportService.atualizarReport();
        return "***** REPORT ATUALIZADO! *****";
    }
}
