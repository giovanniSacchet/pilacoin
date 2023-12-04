package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bloco")
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "numero_bloco")
    private Long numeroBloco;

    @Column(name = "nonce")
    private String nonce;

    @Column(name = "nonce_bloco_anterior")
    private BigInteger nonceBlocoAnterior;

    @Column(name = "chave_usuario_minerador")
    private byte[] chaveUsuarioMinerador;

    @Column(name = "nome_usuario_minerador")
    private String nomeUsuarioMinerador;

    @OneToMany(mappedBy = "bloco", cascade = CascadeType.ALL)
    private List<Transacao> transacoes;
}
