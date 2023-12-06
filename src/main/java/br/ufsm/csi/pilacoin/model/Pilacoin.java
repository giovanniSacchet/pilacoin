package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "pilacoin")
public class Pilacoin {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nonce", unique = true)
    private String nonce;

    @Column(name = "data_criacao")
    private Date dataCriacao;

    @Column(name = "chave_criador")
    private byte[] chaveCriador;

    @Column(name = "nome_criador")
    private String nomeCriador;

    @Column(name = "status")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pilacoin", fetch = FetchType.EAGER)
    private List<TransferirPila> transacoes;

}
