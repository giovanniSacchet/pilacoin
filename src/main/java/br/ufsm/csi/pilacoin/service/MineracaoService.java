package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Bloco;
import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private volatile boolean minerandoPila = true;
    private volatile boolean minerandoBloco = false;
    public MineracaoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public static String getNonce(){
        Random nonce = new Random();
        byte[] bytes = new byte[256/8];
        nonce.nextBytes(bytes);
        return new BigInteger(bytes).abs().toString();
    }

    @SneakyThrows
    public void minerarPilacoin() {
        this.minerandoPila = true;
        Pilacoin pila = Pilacoin.builder().
                chaveCriador(KeyUtil.publicKey.getEncoded()).
                dataCriacao(new Date()).
                nomeCriador("gxs").build();

        ObjectMapper objectMapper = new ObjectMapper();
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger hash;
        String pilaJson = "";

        while (this.minerandoPila) {
            if (DificuldadeService.dificuldadeAtual != null) {
                pila.setNonce(getNonce());
                pilaJson = objectMapper.writeValueAsString(pila);
                hash = new BigInteger(md.digest(pilaJson.getBytes(StandardCharsets.UTF_8))).abs();

                if (hash.compareTo(DificuldadeService.dificuldadeAtual.abs()) < 0){
                    System.out.println(pilaJson);
                    this.rabbitTemplate.convertAndSend("pila-minerado", pilaJson);
                }
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
        bloco.setNomeUsuarioMinerador("gxs");
        bloco.setChaveUsuarioMinerador(KeyUtil.publicKey.getEncoded());
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        BigInteger hash;
        String blocoJson = "";

        while(this.minerandoBloco){
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
