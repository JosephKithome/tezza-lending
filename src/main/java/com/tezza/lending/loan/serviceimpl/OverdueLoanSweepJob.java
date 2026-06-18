package com.tezza.lending.loan.serviceimpl;

import com.tezza.lending.customer.model.*;
import com.tezza.lending.customer.repository.*;
import com.tezza.lending.loan.model.*;
import com.tezza.lending.loan.repository.*;
import com.tezza.lending.loan.service.*;
import com.tezza.lending.notification.model.*;
import com.tezza.lending.notification.service.*;
import com.tezza.lending.product.model.*;
import com.tezza.lending.product.repository.*;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class OverdueLoanSweepJob {
    private final LoanService loanService;

    public OverdueLoanSweepJob(LoanService loanService) {
        this.loanService = loanService;
    }

    @Scheduled(cron = "${lending.sweep.overdue-cron}")
    public void sweep() {
        loanService.sweepOverdue(LocalDate.now());
    }

    @Scheduled(cron = "${lending.sweep.due-reminder-cron}")
    public void sendDueDateReminders() {
        loanService.sendDueDateReminders(LocalDate.now(), 3);
    }
}
