package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.*;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class ValidacaoService {

    private final RabbitTemplate rabbitTemplate;
    public volatile boolean validandoPila = false;
    public volatile boolean validandoBloco = false;


    @Autowired
    public ValidacaoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @SneakyThrows
    @RabbitListener(queues = "pila-minerado")
    public void validarPila(@Payload String pilaStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        Pilacoin pilacoin = objectMapper.readValue(pilaStr, Pilacoin.class);

        if(!validandoPila || pilacoin.getNomeCriador().equals("gxs")) {
            rabbitTemplate.convertAndSend("pila-minerado", pilaStr);
        } else {

            System.out.println("\n\n***** VALIDANDO PILA *****\n\tCRIADOR: " + pilacoin.getNomeCriador());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger hash = new BigInteger(md.digest(pilaStr.getBytes(StandardCharsets.UTF_8))).abs();

            if (DificuldadeService.dificuldadeAtual != null) {
                if(hash.compareTo(DificuldadeService.dificuldadeAtual.abs()) < 0) {
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, KeyUtil.privateKey);
                    byte[] assinatura = md.digest(pilaStr.getBytes(StandardCharsets.UTF_8));
                    PilaValidado pilaValidado = PilaValidado.builder().
                            pilaCoinJson(pilacoin).
                            assinaturaPilaCoin(cipher.doFinal(assinatura)).
                            nomeValidador("gxs").
                            chavePublicaValidador(KeyUtil.publicKey.getEncoded()).build();
                    rabbitTemplate.convertAndSend("pila-validado", objectMapper.writeValueAsString(pilaValidado));
                    System.out.println("***** PILA VALIDADO! *****");
                } else {
                    System.out.println("\n\n================== ERRO AO VALIDAR PILA DE " + pilacoin.getNomeCriador() + " ==================");
                    rabbitTemplate.convertAndSend("pila-minerado", pilaStr);
                }
            }
        }
    }

    @SneakyThrows
    @RabbitListener(queues = "bloco-minerado")
    public void validarBloco(@Payload String blocoStr) {
        System.out.println(blocoStr);
        Bloco bloco;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            bloco = objectMapper.readValue(blocoStr, Bloco.class);
        } catch (Exception e) {
            e.printStackTrace();
            rabbitTemplate.convertAndSend("bloco-minerado", blocoStr);
            System.out.println("\n\n***** BLOCO INVALIDO! *****");
            return;
        }

        if (!validandoBloco || bloco.getNomeUsuarioMinerador().equals("gxs")) {
            rabbitTemplate.convertAndSend("bloco-minerado", blocoStr);
            return;
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        BigInteger hash = new BigInteger(md.digest(blocoStr.getBytes(StandardCharsets.UTF_8))).abs();

        if (DificuldadeService.dificuldadeAtual != null && bloco != null) {
            if (hash.compareTo(DificuldadeService.dificuldadeAtual.abs()) < 0) {

                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, KeyUtil.privateKey);
                byte[] assinatura = md.digest(blocoStr.getBytes(StandardCharsets.UTF_8));
                BlocoValidado blocoValidado = BlocoValidado.builder().
                        assinaturaBloco(cipher.doFinal(assinatura)).
                        bloco(bloco).
                        chavePublicaValidador(KeyUtil.publicKey.getEncoded()).
                        nomeValidador("gxs").build();
                rabbitTemplate.convertAndSend("bloco-validado", objectMapper.writeValueAsString(blocoValidado));

            } else {
                rabbitTemplate.convertAndSend("bloco-minerado", blocoStr);
            }
        }
    }

    public void iniciarValidacaoPila() {
        validandoPila = true;
    }
    public void pararValidacaoPila() {
        validandoPila = false;
    }
    public void iniciarValidacaoBloco() {
        validandoBloco = true;
    }
    public void pararValidacaoBloco() {
        validandoBloco = false;
    }

}
