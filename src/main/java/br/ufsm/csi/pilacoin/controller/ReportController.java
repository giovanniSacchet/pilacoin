package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.EnviarQueryServidor;
import br.ufsm.csi.pilacoin.model.Report;
import br.ufsm.csi.pilacoin.service.ReportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@CrossOrigin
public class ReportController {

    private final ReportService reportService;
    private final RabbitTemplate rabbitTemplate;

    public ReportController(ReportService reportService, RabbitTemplate rabbitTemplate) {
        this.reportService = reportService;
        this.rabbitTemplate = rabbitTemplate;
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

    @GetMapping("/query/pila")
    public void salvarPilasBanco() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        rabbitTemplate.convertAndSend("query", objectMapper.writeValueAsString(EnviarQueryServidor.builder().
                tipoQuery("PILA").idQuery(37).nomeUsuario("gxs").usuarioMinerador("gxs").build()));
    }

    @GetMapping("/query/bloco")
    public void salvarBlocosBanco() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        rabbitTemplate.convertAndSend("query", objectMapper.writeValueAsString(EnviarQueryServidor.builder().
                tipoQuery("BLOCO").idQuery(38).nomeUsuario("gxs").build()));
    }

    @GetMapping("/query/usuario")
    public void salvarUsuariosBanco() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        rabbitTemplate.convertAndSend("query", objectMapper.writeValueAsString(EnviarQueryServidor.builder().
                tipoQuery("USUARIOS").idQuery(39).nomeUsuario("gxs").build()));
    }
}
