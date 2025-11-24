package br.com.furb.rotasegura.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.furb.rotasegura.domain.entities.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {

    boolean existsByCouponCodeIgnoreCase(String couponCode);
    
    boolean existsByNameIgnoreCase(String name);
}
