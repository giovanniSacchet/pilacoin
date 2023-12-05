package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {
    private Date geradoEm;
    private String nomeUsuario;
    private boolean minerouPila;
    private boolean validouPila;
    private boolean minerouBloco;
    private boolean validouBloco;
    private boolean transferiuPila;
}
