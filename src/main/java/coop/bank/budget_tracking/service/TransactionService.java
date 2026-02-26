package coop.bank.budget_tracking.service;


import coop.bank.budget_tracking.dto.request.CategoryFilterRequest;
import coop.bank.budget_tracking.dto.response.TransactionResponse;
import coop.bank.budget_tracking.entity.Transaction;
import coop.bank.budget_tracking.exceptions.TransactionNotFoundException;
import coop.bank.budget_tracking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction Service - Business logic for transactions
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Get paginated transactions for customer
     */
    public Page<TransactionResponse> getTransactionsByCustomer(
            String cifId, int page, int size) {

        log.debug("Fetching transactions for CIF: {}, page: {}, size: {}",
                cifId, page, size);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "transactionDate"));

        return transactionRepository
                .findByCifIdOrderByTransactionDateDesc(cifId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get transactions with filters
     */
    public Page<TransactionResponse> getTransactionsWithFilter(
            CategoryFilterRequest request) {

        log.debug("Fetching filtered transactions for CIF: {}", request.getCifId());

        OffsetDateTime startDateTime = request.getStartDate().atStartOfDay()
                .atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime endDateTime = request.getEndDate().atTime(23, 59, 59)
                .atOffset(OffsetDateTime.now().getOffset());

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.valueOf(request.getSortDirection()),
                        request.getSortBy())
        );

        Page<Transaction> transactions;

        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            // Filter by category
            transactions = transactionRepository.findExpensesByCategory(
                            request.getCifId(),
                            request.getCategory(),
                            startDateTime,
                            endDateTime
                    ).stream()
                    .skip((long) request.getPage() * request.getSize())
                    .limit(request.getSize())
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(
                                    list, pageable, list.size()
                            )
                    ));
        } else {
            // All transactions in date range
            transactions = transactionRepository.findByCifIdAndDateRangePaginated(
                    request.getCifId(),
                    startDateTime,
                    endDateTime,
                    pageable
            );
        }

        return transactions.map(this::mapToResponse);
    }

    /**
     * Get uncategorized transactions
     */
    public List<TransactionResponse> getUncategorizedTransactions(
            String cifId, int months) {

        log.debug("Fetching uncategorized transactions for CIF: {}", cifId);

        OffsetDateTime fromDate = OffsetDateTime.now().minusMonths(months);

        return transactionRepository
                .findUncategorizedTransactions(cifId, fromDate)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate category spending
     */
    public BigDecimal calculateCategorySpending(
            String cifId, String category,
            OffsetDateTime startDate, OffsetDateTime endDate) {

        log.debug("Calculating spending for CIF: {}, category: {}",
                cifId, category);

        return transactionRepository.calculateTotalSpendingByCategory(
                cifId, category, startDate, endDate);
    }

    /**
     * Re-categorize transaction
     */
    @Transactional
    public TransactionResponse recategorizeTransaction(
            Long transactionId, String newCategory) {

        log.info("Re-categorizing transaction {}: {}", transactionId, newCategory);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with ID: " + transactionId));

        transaction.setCategory(newCategory);
        transaction.setConfidence(1.0); // Manual categorization = 100% confidence

        Transaction updated = transactionRepository.save(transaction);

        return mapToResponse(updated);
    }

    /**
     * Get transaction by ID
     */
    public TransactionResponse getTransactionById(Long id) {
        log.debug("Fetching transaction ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with ID: " + id));

        return mapToResponse(transaction);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Map Transaction entity to TransactionResponse DTO
     */
    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .transactionDate(transaction.getTransactionDate())
                .postedDate(transaction.getPostedDate())
                .merchant(transaction.getMerchant())
                .transactionParticular(transaction.getTransactionParticular())
                .amount(transaction.getTransactionAmount())
                .transactionType(transaction.getPartTransactionType())
                .category(transaction.getCategory())
                .subcategory(transaction.getSubcategory())
                .confidence(transaction.getConfidence())
                .build();
    }
}
