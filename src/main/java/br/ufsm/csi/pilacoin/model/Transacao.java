package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_usuario_origem")
    private String chaveUsuarioOrigem;

    @Column(name = "chave_usuario_destino")
    private String chaveUsuarioDestino;

    @Column(name = "assinatura")
    private String assinatura;

    @Column(name = "nonce_pila")
    private String noncePila;

    @Column(name = "data_transacao")
    private Date dataTransacao;

    @Column(name = "status")
    private String status;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "bloco_id")
    private Bloco bloco;

}
