package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlocoRepository extends JpaRepository<Bloco, String> {
}
