package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.TransacaoBloco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoBlocoRepository extends JpaRepository<TransacaoBloco, String> {
}
