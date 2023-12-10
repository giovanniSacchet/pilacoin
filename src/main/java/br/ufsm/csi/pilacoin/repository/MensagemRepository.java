package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemRepository extends JpaRepository<Mensagem, String> {
}
