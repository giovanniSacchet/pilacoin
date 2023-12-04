package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloco")
public class Bloco {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_bloco")
    private Long numeroBloco;

    @Column(name = "nonce_bloco_anterior")
    private BigInteger nonceBlocoAnterior;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "chave_usuario_minerador")
    private byte[] chaveUsuarioMinerador;

    @Column(name = "nome_usuario_minerador")
    private String nomeUsuarioMinerador;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bloco", fetch = FetchType.EAGER)//TENTAR USAR O .LAZY TBM
    private List<Transacao> transacoes;
}
