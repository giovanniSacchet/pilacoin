package br.ufsm.csi.pilacoin.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", unique = true)
    private String nome;

    @Column(name = "chave_publica")
    private byte[] chavePublica;
}
