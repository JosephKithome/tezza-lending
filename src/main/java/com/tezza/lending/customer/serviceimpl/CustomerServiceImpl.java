package com.tezza.lending.customer.serviceimpl;

import com.tezza.lending.customer.model.Customer;
import com.tezza.lending.customer.model.CustomerRequest;
import com.tezza.lending.customer.model.CustomerResponse;
import com.tezza.lending.customer.model.LoanLimitRequest;
import com.tezza.lending.customer.repository.CustomerRepository;
import com.tezza.lending.customer.service.CustomerService;
import com.tezza.lending.exception.BusinessRuleException;
import com.tezza.lending.exception.ResourceNotFoundException;
import com.tezza.lending.loan.repository.LoanRepository;
import com.tezza.lending.notification.service.NotificationService;
import com.tezza.lending.shared.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    public CustomerServiceImpl(CustomerRepository repository,
                               LoanRepository loanRepository,
                               NotificationService notificationService) {
        this.repository = repository;
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        String preferredChannel = isBlank(request.preferredNotificationChannel())
                ? "EMAIL"
                : request.preferredNotificationChannel().toUpperCase();

        validatePreferredChannel(preferredChannel);

        Customer customer = new Customer();
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setLoanLimit(request.loanLimit() == null ? BigDecimal.ZERO : request.loanLimit());
        customer.setRiskGrade(isBlank(request.riskGrade()) ? "UNRATED" : request.riskGrade().toUpperCase());
        customer.setPreferredNotificationChannel(preferredChannel);
        customer.setRegistrationOtp(generateOtp());
        customer.setRegistrationOtpExpiresAt(Instant.now().plusSeconds(10 * 60));
        Customer saved = repository.save(customer);
        notificationService.publishCustomerRegistration(saved);
        return CustomerResponse.from(saved);
    }

    @Override
    public CustomerResponse updateLimit(Long id, LoanLimitRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setLoanLimit(request.loanLimit());
        return CustomerResponse.from(repository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return repository.findById(id)
                .map(CustomerResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse list(Pageable pageable) {
        return PagedResponse.from(repository.findAll(pageable).map(CustomerResponse::from));
    }

    @Override
    public void delete(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (loanRepository.countByCustomerId(id) > 0) {
            throw new BusinessRuleException("Customer cannot be deleted because loans are linked to the profile");
        }
        repository.delete(customer);
    }

    private String generateCustomerNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String candidate;

        do {
            candidate = "CUST-" + date + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (repository.findByCustomerNumber(candidate).isPresent());

        return candidate;
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void validatePreferredChannel(String channel) {
        try {
            com.tezza.lending.notification.model.NotificationChannel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Unsupported preferred notification channel");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
