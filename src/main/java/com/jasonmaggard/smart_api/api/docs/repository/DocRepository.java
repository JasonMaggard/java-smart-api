package com.jasonmaggard.smart_api.api.docs.repository;

import com.jasonmaggard.smart_api.api.docs.entity.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocRepository extends JpaRepository<Doc, UUID> {
    Optional<Doc> findByEndpointPathAndHttpMethod(String endpointPath, String httpMethod);
}
