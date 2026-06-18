ALTER TABLE repayments ADD COLUMN IF NOT EXISTS applied_amount DECIMAL(19,2);
ALTER TABLE repayments ADD COLUMN IF NOT EXISTS overpayment_amount DECIMAL(19,2);

UPDATE repayments
SET applied_amount = amount
WHERE applied_amount IS NULL;

UPDATE repayments
SET overpayment_amount = 0.00
WHERE overpayment_amount IS NULL;

UPDATE notification_templates
SET active = FALSE
WHERE event_type IN ('REPAYMENT_ACKNOWLEDGEMENT', 'LOAN_FULLY_REPAID')
  AND channel = 'EMAIL';

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES (
    'REPAYMENT_ACKNOWLEDGEMENT',
    'EMAIL',
    'Repayment receipt - loan {{loanNumber}}',
    '<!doctype html>
<html>
  <body style="margin:0;background:#eef3f8;font-family:Arial,Helvetica,sans-serif;color:#172033;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#eef3f8;padding:28px 0;">
      <tr>
        <td align="center">
          <table role="presentation" width="640" cellspacing="0" cellpadding="0" style="max-width:640px;width:100%;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #d8e2ee;">
            <tr><td style="background:#0f5f8f;padding:28px 32px;color:#ffffff;"><div style="font-size:13px;letter-spacing:.08em;text-transform:uppercase;opacity:.9;">Tezza Lending</div><div style="font-size:26px;font-weight:700;margin-top:8px;">Repayment receipt</div><div style="font-size:14px;margin-top:8px;opacity:.9;">Loan {{loanNumber}}</div></td></tr>
            <tr>
              <td style="padding:30px 32px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 20px;">Hello {{firstName}}, we have received your repayment. The full amount received and the amount applied to your loan are shown separately.</p>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:0 0 22px;">
                  <tr><td colspan="2" style="padding:0 0 10px;font-size:13px;color:#0f5f8f;font-weight:700;text-transform:uppercase;letter-spacing:.06em;">Loan details</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Customer Number</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{customerNumber}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Product</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{productName}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Principal</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{principalAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Due Date</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{dueDate}}</td></tr>
                </table>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:0 0 22px;">
                  <tr><td colspan="2" style="padding:0 0 10px;font-size:13px;color:#0f5f8f;font-weight:700;text-transform:uppercase;letter-spacing:.06em;">Payment allocation</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Amount Received</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{repaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Applied To Loan</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;color:#0f5f8f;">{{appliedAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Overpayment / Unallocated</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;color:#9b4d00;">{{overpaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Previous Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{previousBalance}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Remaining Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{remainingBalance}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Reference</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentReference}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">Any overpayment shown above is recorded for reconciliation or refund processing.</p>
              </td>
            </tr>
            <tr><td style="background:#f8fafc;padding:18px 32px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td></tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>',
    TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES (
    'LOAN_FULLY_REPAID',
    'EMAIL',
    'Congratulations - loan {{loanNumber}} is fully repaid',
    '<!doctype html>
<html>
  <body style="margin:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#172033;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#f4f7fb;padding:28px 0;">
      <tr>
        <td align="center">
          <table role="presentation" width="620" cellspacing="0" cellpadding="0" style="max-width:620px;width:100%;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #dde5f0;">
            <tr><td style="background:#0f5f8f;padding:28px 30px;color:#ffffff;"><div style="font-size:13px;letter-spacing:.08em;text-transform:uppercase;opacity:.85;">Tezza Lending</div><div style="font-size:26px;font-weight:700;margin-top:8px;">Congratulations, {{firstName}}</div></td></tr>
            <tr>
              <td style="padding:30px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 18px;">You have fully repaid loan {{loanNumber}}. Thank you for keeping your commitment.</p>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:18px 0;">
                  <tr><td style="padding:10px 0;color:#526173;">Product</td><td style="padding:10px 0;text-align:right;">{{productName}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Principal</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{principalAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Last Amount Received</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Last Amount Applied</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{appliedAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Overpayment / Unallocated</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;color:#9b4d00;">{{overpaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Total Received</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{totalReceived}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Total Applied To Loan</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{totalApplied}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Closing Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{remainingBalance}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">This repayment report records any unallocated overpayment for reconciliation or refund processing.</p>
              </td>
            </tr>
            <tr><td style="background:#f8fafc;padding:18px 30px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td></tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>',
    TRUE
);
