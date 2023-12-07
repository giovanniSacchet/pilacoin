package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.model.Dificuldade;
import br.ufsm.csi.pilacoin.shared.DificuldadeDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;

@Service
public class DificuldadeService {
    public static BigInteger dificuldadeAtual;


    @SneakyThrows
    @RabbitListener(queues = {"${queue.dificuldade}"})
    public void getDificuldadeServidor(@Payload String dificuldadeServidor) {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addDeserializer(Dificuldade.class, new DificuldadeDeserializer());
        mapper.registerModule(module);

        Dificuldade dif = mapper.readValue(dificuldadeServidor, Dificuldade.class);

        if (!dif.getDificuldade().equals(dificuldadeAtual)) {
            dificuldadeAtual = dif.getDificuldade();
            //System.out.println("\n\n******* ATUALIZOU DIFICULDADE ****** \n\n " + dif.getDificuldade());
        }

    }
}
