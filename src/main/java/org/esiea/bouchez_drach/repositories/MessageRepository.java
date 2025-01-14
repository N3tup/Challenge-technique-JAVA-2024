package org.esiea.bouchez_drach.repositories;

import org.esiea.bouchez_drach.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
