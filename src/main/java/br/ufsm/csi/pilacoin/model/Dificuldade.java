package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.math.BigInteger;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dificuldade {

    private BigInteger dificuldade;

    private Date inicio;

    private Date validadeFinal;

}
