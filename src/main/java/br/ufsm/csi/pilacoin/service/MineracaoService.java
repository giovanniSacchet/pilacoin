package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Bloco;
import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.model.Transacao;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilacoinRepository;
import br.ufsm.csi.pilacoin.repository.TransacaoRepository;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
public class MineracaoService {

    private final RabbitTemplate rabbitTemplate;
    private final PilacoinRepository pilacoinRepository;
    private final BlocoRepository blocoRepository;
    private final TransacaoRepository transacaoRepository;
    private volatile boolean minerandoPila = true;
    private volatile boolean minerandoBloco = false;
    public MineracaoService(RabbitTemplate rabbitTemplate,
                            PilacoinRepository pilacoinRepository,
                            BlocoRepository blocoRepository,
                            TransacaoRepository transacaoRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.pilacoinRepository = pilacoinRepository;
        this.blocoRepository = blocoRepository;
        this.transacaoRepository = transacaoRepository;
    }

    public static String getNonce(){
        Random nonce = new Random();
        byte[] bytes = new byte[256/8];
        nonce.nextBytes(bytes);
        return new BigInteger(bytes).abs().toString();
    }

    @SneakyThrows
    public void minerarPilacoin() {
        Pilacoin pila = Pilacoin.builder().
                chaveCriador(KeyUtil.publicKey.getEncoded()).
                dataCriacao(new Date()).
                nomeCriador("giovanni").build();

        ObjectMapper objectMapper = new ObjectMapper();
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger hash;
        int count = 0;
        String pilaJson = "";

        while (true){
            if (DificuldadeService.dificuldadeAtual != null && this.minerandoPila) {

                count++;
                pila.setNonce(getNonce());
                pilaJson = objectMapper.writeValueAsString(pila);
                hash = new BigInteger(md.digest(pilaJson.getBytes(StandardCharsets.UTF_8))).abs();

                if (hash.compareTo(DificuldadeService.dificuldadeAtual.abs()) < 0){
                    System.out.println("\n\n****** PILA MINERADO ****** \n\tEm " + count + " Tentativas");
                    System.out.println(pilaJson);
                    this.rabbitTemplate.convertAndSend("pila-minerado", pilaJson);

                    this.pilacoinRepository.save(Pilacoin.builder().
                            nonce(pila.getNonce()).
                            dataCriacao(new Date()).
                            chaveCriador(KeyUtil.publicKey.getEncoded()).
                            nomeCriador("giovanni").
                            build());
                    count = 0;
                }
            } else {
                while (!this.minerandoPila) {}
            }
        }
    }

    @SneakyThrows
    @RabbitListener(queues = "descobre-bloco")
    public void minerarBloco(@Payload String blocoStr) {
        if (!this.minerandoBloco) {
            this.rabbitTemplate.convertAndSend("descobre-bloco", blocoStr);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Bloco bloco = objectMapper.readValue(blocoStr, Bloco.class);
        bloco.setNomeUsuarioMinerador("giovanni");
        bloco.setChaveUsuarioMinerador(KeyUtil.publicKey.getEncoded());
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger hash;
        String blocoJson = "";

        while(true){
            bloco.setNonceBlocoAnterior(bloco.getNonce());
            bloco.setNonce(getNonce());

            objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            blocoJson = objectMapper.writeValueAsString(bloco);
            hash = new BigInteger(md.digest(blocoJson.getBytes(StandardCharsets.UTF_8))).abs();

            if (DificuldadeService.dificuldadeAtual != null) {
                if (hash.compareTo(DificuldadeService.dificuldadeAtual.abs()) < 0){
                    System.out.println("\n\n***** BLOCO MINERADO *****\n\t"+blocoJson);
                    this.rabbitTemplate.convertAndSend("bloco-minerado", blocoJson);

                    for (Transacao t : bloco.getTransacoes()) {
                        t.setBloco(bloco);
                        this.transacaoRepository.save(t);
                    }

                    this.blocoRepository.save(bloco);
                    return;
                }
            }
        }
    }

    public void pararMineracaoPila(){
        minerandoPila = false;
    }

    public void iniciarMineracaoBloco(){
        minerandoBloco = true;
    }

    public void pararMineracaoBloco(){
        minerandoBloco = false;
    }
}
