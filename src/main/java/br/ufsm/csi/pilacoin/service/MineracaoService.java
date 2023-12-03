package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.model.PilacoinStatusEnum;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

@Service
public class MineracaoService implements Runnable {

    private final RabbitTemplate rabbitTemplate;
    private final PilacoinRepository pilacoinRepository;
    private final DificuldadeService dificuldadeService;
    private volatile boolean minerando = true;
    public MineracaoService(RabbitTemplate rabbitTemplate, PilacoinRepository pilacoinRepository, DificuldadeService dificuldadeService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinRepository = pilacoinRepository;
        this.dificuldadeService = dificuldadeService;
    }

    public static String gerarNonce(){
        Random nonce = new Random();
        byte[] bytes = new byte[256/8];
        nonce.nextBytes(bytes);
        return new BigInteger(bytes).abs().toString();
    }

    @SneakyThrows
    public void run() {
        System.out.println("\n\n***** MINERANDO... *****");
        Pilacoin pila = Pilacoin.builder().
                                dataCriacao(new Date()).
                                chaveCriador(KeyUtil.publicKey.getEncoded()).
                                nomeCriador("giovanni").build();
        ObjectMapper obj = new ObjectMapper();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash;
        int tentativa = 0;
        while (true){
            if (this.dificuldadeService.getDificuldadeAtual() != null) {
                tentativa++;
                if(!minerando){
                    while (!minerando){}
                }
                pila.setNonce(gerarNonce());
                hash = new BigInteger(md.digest(obj.writeValueAsString(pila).getBytes(StandardCharsets.UTF_8))).abs();
                if (hash.compareTo(this.dificuldadeService.getDificuldadeAtual()) < 0){
                    System.out.println("\n\n****** PILA MINERADO ****** \n\tTentativas: " + tentativa);
                    tentativa = 0;
                    rabbitTemplate.convertAndSend("pila-minerado", obj.writeValueAsString(pila));
                    pilacoinRepository.save(Pilacoin.builder().
                            nonce(pila.getNonce()).
                            status(PilacoinStatusEnum.AG_VALIDACAO.toString()).
                            dataCriacao(new Date()).
                            chaveCriador(KeyUtil.publicKey.getEncoded()).
                            nomeCriador("giovanni").
                            build());
                }
            }
        }
    }

    public void pararMineracao(){
        minerando = false;
    }

    public void iniciarMineracao(){
        minerando = true;
    }
}
