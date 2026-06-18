INSERT INTO loan_products (id, code, name, minimum_amount, maximum_amount, tenure_value, tenure_unit, days_after_due_for_fee_application, active)
VALUES
    (1, 'PAYDAY-30', '30 Day Payday Loan', 1000.00, 50000.00, 30, 'DAYS', 3, TRUE),
    (2, 'SME-12M', '12 Month SME Working Capital', 10000.00, 500000.00, 12, 'MONTHS', 5, TRUE);

INSERT INTO product_fees (product_id, fee_type, calculation_type, application_stage, fee_value, trigger_days_after_due)
VALUES
    (1, 'SERVICE_FEE', 'PERCENTAGE', 'ORIGINATION', 5.00, NULL),
    (1, 'DAILY_FEE', 'PERCENTAGE', 'DAILY_ACCRUAL', 0.50, NULL),
    (1, 'LATE_FEE', 'FIXED', 'AFTER_DUE_DATE', 750.00, 3),
    (2, 'SERVICE_FEE', 'PERCENTAGE', 'ORIGINATION', 3.00, NULL),
    (2, 'LATE_FEE', 'PERCENTAGE', 'AFTER_DUE_DATE', 2.00, 5);

INSERT INTO customers (id, customer_number, first_name, last_name, email, phone_number, date_of_birth, loan_limit, risk_grade, preferred_notification_channel)
VALUES
    (1, 'CUST-001', 'Amina', 'Otieno', 'amina.otieno@example.com', '+254700000001', '1992-04-12', 75000.00, 'A', 'EMAIL'),
    (2, 'CUST-002', 'Brian', 'Mwangi', 'brian.mwangi@example.com', '+254700000002', '1988-09-22', 300000.00, 'B', 'SMS');

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES
    ('LOAN_CREATED', 'EMAIL', 'Loan {{loanNumber}} created', 'Hello {{firstName}}, your loan {{loanNumber}} has been created. Amount due is {{amount}} by {{dueDate}}.', TRUE),
    ('LOAN_CREATED', 'SMS', 'Loan created', 'Hello {{firstName}}, loan {{loanNumber}} is active. Due {{dueDate}}.', TRUE),
    ('REPAYMENT_ACKNOWLEDGEMENT', 'EMAIL', 'Repayment received', 'Hello {{firstName}}, repayment for {{loanNumber}} was received. Balance is {{amount}}.', TRUE),
    ('REPAYMENT_ACKNOWLEDGEMENT', 'SMS', 'Repayment received', 'Repayment received for {{loanNumber}}. Balance {{amount}}.', TRUE),
    ('OVERDUE_NOTICE', 'EMAIL', 'Loan {{loanNumber}} is overdue', 'Hello {{firstName}}, your loan {{loanNumber}} is overdue. Balance is {{amount}}.', TRUE),
    ('OVERDUE_NOTICE', 'SMS', 'Loan overdue', 'Loan {{loanNumber}} is overdue. Balance {{amount}}.', TRUE);

INSERT INTO notification_rules (event_type, product_code, customer_segment, channel, active)
VALUES
    ('LOAN_CREATED', 'PAYDAY-30', 'A', 'EMAIL', TRUE),
    ('OVERDUE_NOTICE', 'PAYDAY-30', 'A', 'SMS', TRUE),
    ('REPAYMENT_ACKNOWLEDGEMENT', 'SME-12M', 'B', 'SMS', TRUE);

ALTER TABLE loan_products ALTER COLUMN id RESTART WITH 3;
ALTER TABLE customers ALTER COLUMN id RESTART WITH 3;
