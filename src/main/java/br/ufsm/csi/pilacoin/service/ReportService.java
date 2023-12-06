package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.*;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.repository.TransferirPilaRepository;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    private boolean atualizarReport = true;
    private Report meuReport = new Report();
    private final PilacoinRepository pilacoinRepository;
    private final BlocoRepository blocoRepository;
    private final TransferirPilaRepository transferirPilaRepository;
    private final UsuarioRepository usuarioRepository;
    public ReportService(PilacoinRepository pilacoinRepository,
                         BlocoRepository blocoRepository,
                         TransferirPilaRepository transferirPilaRepository,
                         UsuarioRepository usuarioRepository) {
        this.pilacoinRepository = pilacoinRepository;
        this.blocoRepository = blocoRepository;
        this.transferirPilaRepository = transferirPilaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @RabbitListener(queues = "report")
    public void getReportServidor(@Payload String json) throws JsonProcessingException {
        if (this.atualizarReport) {
            ObjectMapper objectMapper = new ObjectMapper();
            Report[] reportsArray = objectMapper.readValue(json, Report[].class);
            List<Report> reportsList = Arrays.asList(reportsArray);
            for (Report r : reportsList) {
                if (r != null && r.getNomeUsuario() != null) {
                    if (r.getNomeUsuario().equals("gxs")) {
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

    private void salvarPilaBanco(Pilacoin pilacoin) {
        if (pilacoin.getTransacoes() != null && !pilacoin.getTransacoes().isEmpty()) {
            for (TransferirPila t : pilacoin.getTransacoes()) {
                this.transferirPilaRepository.saveAll(pilacoin.getTransacoes());
            }
        }
        this.pilacoinRepository.save(Pilacoin.builder().
                nonce(pilacoin.getNonce()).
                nomeCriador(pilacoin.getNomeCriador()).
                chaveCriador(pilacoin.getChaveCriador()).
                dataCriacao(pilacoin.getDataCriacao()).
                status(pilacoin.getStatus()).
                build());
    }

    private void salvarBlocoBanco(Bloco bloco) {
        /*if (bloco.getTransacoes() != null && !bloco.getTransacoes().isEmpty()) {
            for (TransferirPila t : bloco.getTransacoes()) {
                this.transferirPilaRepository.saveAll(bloco.getTransacoes());
            }
        }
        this.blocoRepository.save(Bloco.builder().
                nonce(pilacoin.getNonce()).
                nomeCriador(pilacoin.getNomeCriador()).
                chaveCriador(pilacoin.getChaveCriador()).
                dataCriacao(pilacoin.getDataCriacao()).
                status(pilacoin.getStatus()).
                build());*/
    }

    private void salvarUsuarioBanco(Usuario usuario) {
        this.usuarioRepository.save(Usuario.builder().
                id(usuario.getId()).
                nome(usuario.getNome()).
                chavePublica(usuario.getChavePublica()).build());
    }

    private void salvarPilaQuery(List<Pilacoin> pilaList) {
        List<Pilacoin> pilasArmazenados = this.pilacoinRepository.findAll();
        for (Pilacoin pila : pilaList) {
            if (!pilasArmazenados.isEmpty()) {// Se ja tiver algum pila no banco
                Optional<Pilacoin> pilaBanco = this.pilacoinRepository.findByNonce(pila.getNonce()); // Verifica se esse pila ja esta no banco
                if (!pilaBanco.isPresent()) {
                    this.salvarPilaBanco(pila);
                }
            } else {
                this.salvarPilaBanco(pila);
            }
        }
    }

    private void salvarBlocoQuery(List<Bloco> blocoList) {
        List<Bloco> blocosArmazenados = this.blocoRepository.findAll();
        for (Bloco bloco : blocoList) {
            if (!blocosArmazenados.isEmpty()) {// Se ja tiver algum bloco no banco
                Optional<Bloco> blocoBanco = this.blocoRepository.findByNonce(bloco.getNonce()); // Verifica se esse bloco ja esta no banco
                if (!blocoBanco.isPresent()) {
                    this.salvarBlocoBanco(bloco);
                }
            } else {
                this.salvarBlocoBanco(bloco);
            }
        }
    }

    private void salvarUsuarioQuery(List<Usuario> usuarioList) {
        List<Usuario> usuariosArmazenados = this.usuarioRepository.findAll();
        for (Usuario usuario : usuarioList) {
            if (!usuariosArmazenados.isEmpty()) {// Se ja tiver algum usuario no banco
                Optional<Usuario> usuarioBanco = this.usuarioRepository.findByNome(usuario.getNome()); // Verifica se esse usuario ja esta no banco
                if (!usuarioBanco.isPresent()) {
                    this.salvarUsuarioBanco(usuario);
                }
            } else {
                this.salvarUsuarioBanco(usuario);
            }
        }
    }

    @RabbitListener(queues = "gxs")
    public void mensagens(@Payload String msg){
        System.out.println("-=+=".repeat(10)+"\n"+msg+"\n"+"-=+=".repeat(10));
    }

    @SneakyThrows
    @RabbitListener(queues = "gxs-query")
    public void receberQueryPessoal(@Payload String query) {
        System.out.println(query);
        ReceberQueryServidor receberQueryServidor = new ReceberQueryServidor();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            receberQueryServidor = objectMapper.readValue(query, ReceberQueryServidor.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (receberQueryServidor.getPilasResult() != null && !receberQueryServidor.getPilasResult().isEmpty()) {
            this.salvarPilaQuery(receberQueryServidor.getPilasResult());
        } else if (receberQueryServidor.getBlocosResult() != null && !receberQueryServidor.getBlocosResult().isEmpty()) {
            this.salvarBlocoQuery(receberQueryServidor.getBlocosResult());
        } else if (receberQueryServidor.getUsuariosResult() != null && !receberQueryServidor.getUsuariosResult().isEmpty()) {
            this.salvarUsuarioQuery(receberQueryServidor.getUsuariosResult());
        }
    }

}
