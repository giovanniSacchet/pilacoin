package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transferir_pila")
public class TransferirPila {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_transferir_pila;

    @Column(name = "chave_usuario_origem")
    private String chaveUsuarioOrigem;

    @Column(name = "chave_usuario_destino")
    private String chaveUsuarioDestino;

    @Column(name = "nome_usuario_origem")
    private String nomeUsuarioOrigem;

    @Column(name = "nome_usuario_destino")
    private String nomeUsuarioDestino;

    @Column(name = "assinatura")
    private String assinatura;

    @Column(name = "nonce_pila")
    private String noncePila;

    @Column(name = "data_transacao")
    private Date dataTransacao;

    @Column(name = "id_servidor")
    private String id;

    @Column(name = "status")
    private String status;

}