package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Dificuldade;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;

@Service
public class DificuldadeService {
    public static BigInteger dificuldadeAtual;

    public DificuldadeService() {}
    @SneakyThrows
    @RabbitListener(queues = {"${queue.dificuldade}"})
    public void getDificuldadeServidor(@Payload String dificuldadeServidor) {
        ObjectMapper objectMapper = new ObjectMapper();
        Dificuldade dificuldade = objectMapper.readValue(dificuldadeServidor, Dificuldade.class);
        dificuldadeAtual = new BigInteger(dificuldade.getDificuldade(), 16);
        //System.out.println("\n***** DIFICULDADE ATUAL: " + dificuldadeAtual + " *****");
    }

    public BigInteger getDificuldadeAtual() {
        return dificuldadeAtual;
    }
}
