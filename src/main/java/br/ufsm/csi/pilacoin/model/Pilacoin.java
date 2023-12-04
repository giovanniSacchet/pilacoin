package br.ufsm.csi.pilacoin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pilacoin")
public class Pilacoin {
    @Id
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


}
