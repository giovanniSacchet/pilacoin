package br.ufsm.csi.pilacoin.shared;

import br.ufsm.csi.pilacoin.model.Dificuldade;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

public class DificuldadeDeserializer extends StdDeserializer<Dificuldade> {
    public DificuldadeDeserializer() {
        this(null);
    }

    public DificuldadeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Dificuldade deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String dificuldade = node.get("dificuldade").asText();
        Date inicio = new Date(node.get("inicio").asLong());
        Date validadeFinal = new Date(node.get("validadeFinal").asLong());

        BigInteger diff = new BigInteger(dificuldade, 16).abs();

        return Dificuldade.builder()
                .dificuldade(diff)
                .inicio(inicio)
                .validadeFinal(validadeFinal)
                .build();
    }
}
