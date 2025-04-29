package com.NikolaySHA.ExclusiveService.repo;

import com.NikolaySHA.ExclusiveService.model.entity.TransferProtocol;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocolRepository extends JpaRepository<TransferProtocol, Long> {
    List<TransferProtocol> findAll();
    
    Optional<TransferProtocol> findById(Long id);
}