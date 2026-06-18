INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'CUSTOMER_REGISTERED', 'SMS', 'Welcome to Tezza Lending',
       'Hello {{firstName}}, your Tezza profile is ready. Customer {{customerNumber}}. OTP {{otp}} expires in 10 minutes.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'CUSTOMER_REGISTERED' AND channel = 'SMS' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'CUSTOMER_REGISTERED', 'PUSH', 'Welcome to Tezza Lending',
       'Your customer profile is ready. OTP {{otp}} expires in 10 minutes.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'CUSTOMER_REGISTERED' AND channel = 'PUSH' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'LOAN_CREATED', 'PUSH', 'Loan created',
       'Loan {{loanNumber}} is active. Amount due {{amount}} by {{dueDate}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'LOAN_CREATED' AND channel = 'PUSH' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'LOAN_DISBURSED', 'PUSH', 'Loan disbursed',
       'Loan {{loanNumber}} has been disbursed. Amount due {{amount}} by {{dueDate}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'LOAN_DISBURSED' AND channel = 'PUSH' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'DUE_DATE_REMINDER', 'EMAIL', 'Loan {{loanNumber}} payment reminder',
       '<!doctype html>
<html>
  <body style="margin:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#172033;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#f4f7fb;padding:28px 0;">
      <tr>
        <td align="center">
          <table role="presentation" width="620" cellspacing="0" cellpadding="0" style="max-width:620px;width:100%;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #dde5f0;">
            <tr>
              <td style="background:#0f5f8f;padding:26px 30px;color:#ffffff;">
                <div style="font-size:13px;letter-spacing:.08em;text-transform:uppercase;opacity:.85;">Tezza Lending</div>
                <div style="font-size:25px;font-weight:700;margin-top:8px;">Payment reminder</div>
              </td>
            </tr>
            <tr>
              <td style="padding:30px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 18px;">Hello {{firstName}}, this is a reminder for loan {{loanNumber}}.</p>
                <p style="font-size:15px;line-height:1.6;margin:0 0 18px;">Amount due: <strong>{{amount}}</strong></p>
                <p style="font-size:15px;line-height:1.6;margin:0;">Due date: <strong>{{dueDate}}</strong></p>
              </td>
            </tr>
            <tr>
              <td style="background:#f8fafc;padding:18px 30px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'DUE_DATE_REMINDER' AND channel = 'EMAIL' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'DUE_DATE_REMINDER', 'SMS', 'Payment reminder',
       'Hello {{firstName}}, loan {{loanNumber}} payment of {{amount}} is due on {{dueDate}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'DUE_DATE_REMINDER' AND channel = 'SMS' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'DUE_DATE_REMINDER', 'PUSH', 'Payment reminder',
       'Loan {{loanNumber}} payment of {{amount}} is due on {{dueDate}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'DUE_DATE_REMINDER' AND channel = 'PUSH' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'REPAYMENT_ACKNOWLEDGEMENT', 'PUSH', 'Repayment received',
       'Repayment for loan {{loanNumber}} was received. Balance {{amount}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'REPAYMENT_ACKNOWLEDGEMENT' AND channel = 'PUSH' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'OVERDUE_NOTICE', 'PUSH', 'Loan overdue',
       'Loan {{loanNumber}} is overdue. Balance {{amount}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'OVERDUE_NOTICE' AND channel = 'PUSH' AND active = TRUE
);
