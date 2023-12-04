package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.PilaValidado;
import br.ufsm.csi.pilacoin.model.Pilacoin;
import br.ufsm.csi.pilacoin.shared.KeyUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
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
import java.security.NoSuchAlgorithmException;

@Service
public class ValidacaoService {

    private final RabbitTemplate rabbitTemplate;
    private final DificuldadeService dificuldadeService;
    public static boolean validarPila = false;

    @Autowired
    public ValidacaoService(RabbitTemplate rabbitTemplate, DificuldadeService dificuldadeService) {
        this.rabbitTemplate = rabbitTemplate;
        this.dificuldadeService = dificuldadeService;
    }

    @SneakyThrows
    @RabbitListener(queues = "pila-minerado")
    public void validarPila(@Payload String pilaStr) {
        if (!validarPila) {
            rabbitTemplate.convertAndSend("pila-minerado", pilaStr);
            return;
        }

        ObjectMapper ob = new ObjectMapper();
        Pilacoin pilacoin = ob.readValue(pilaStr, Pilacoin.class);

        if(pilacoin.getNomeCriador().equals("giovanni")){
            rabbitTemplate.convertAndSend("pila-minerado", pilaStr);
        } else {
            System.out.println("\n\n***** VALIDANDO PILA *****\n\tCRIADOR: " + pilacoin.getNomeCriador());

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger hash = new BigInteger(md.digest(pilaStr.getBytes(StandardCharsets.UTF_8))).abs();
            if (this.dificuldadeService.getDificuldadeAtual() != null) {
                if(hash.compareTo(this.dificuldadeService.getDificuldadeAtual()) < 0){
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE, KeyUtil.privateKey);
                    byte[] assinatura = md.digest(pilaStr.getBytes(StandardCharsets.UTF_8));
                    PilaValidado pilaValidado = PilaValidado.builder().
                            pilaCoinJson(pilacoin).
                            assinaturaPilaCoin(cipher.doFinal(assinatura)).
                            nomeValidador("giovanni").
                            chavePublicaValidador(KeyUtil.publicKey.getEncoded()).build();

                    rabbitTemplate.convertAndSend("pila-validado", ob.writeValueAsString(pilaValidado));

                    System.out.println("***** PILA VALIDADO! *****");
                } else {
                    System.out.println("\n\n================== ERRO AO VALIDAR PILA DE " + pilacoin.getNomeCriador() + " ==================");
                    rabbitTemplate.convertAndSend("pila-minerado", pilaStr);
                }

            }
        }
    }

    public void iniciarValidacaoPila(){
        validarPila = true;
    }
    public void pararValidacaoPila(){
        validarPila = false;
    }




}
