package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Report;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {

    private boolean atualizarReport = true;
    private Report meuReport = new Report();

    @RabbitListener(queues = "report")
    public void getReportServidor(@Payload String json) throws JsonProcessingException {
        if (this.atualizarReport) {
            ObjectMapper objectMapper = new ObjectMapper();
            Report[] reportsArray = objectMapper.readValue(json, Report[].class);
            List<Report> reportsList = Arrays.asList(reportsArray);


            for (Report r : reportsList) {
                if (r != null && r.getNomeUsuario() != null) {
                    if (r.getNomeUsuario().equals("giovanni")) {
                        this.meuReport = r;
                        this.atualizarReport = false;
                    }
                }
            }

            System.out.println("\n\n\n***** STATUS TRABALHO *****\n\tMinerou Pila: " + this.meuReport.isMinerouPila() + "\n\tValidou Pila: " + this.meuReport.isValidouPila() + "\n\tMinerou Bloco: " + this.meuReport.isMinerouBloco() + "\n\tValidou Bloco: " + this.meuReport.isValidouBloco() + "\n\tTransferiu Pila: " + this.meuReport.isTransferiuPila() + "\n\n\n");
        }
    }

    public Report getReportAtual() {
        return this.meuReport;
    }

    public void atualizarReport() {
        this.atualizarReport = true;
    }

    @RabbitListener(queues = "giovanni")
    public void mensagens(@Payload String msg) {
        System.out.println("\n\n****************************\n" + msg + "\n****************************\n\n");
    }

}
