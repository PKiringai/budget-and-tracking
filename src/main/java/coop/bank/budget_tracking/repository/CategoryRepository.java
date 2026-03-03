package coop.bank.budget_tracking.repository;

import coop.bank.budget_tracking.entity.Category;
import coop.bank.budget_tracking.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByStatusOrderByNameDesc(Status status);
}
