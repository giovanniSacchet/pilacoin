package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, String> {
}
