UPDATE notification_templates
SET active = FALSE
WHERE event_type = 'REPAYMENT_ACKNOWLEDGEMENT';

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES
    ('REPAYMENT_ACKNOWLEDGEMENT', 'EMAIL', 'Repayment received for loan {{loanNumber}}',
     '<!doctype html>
<html>
  <body style="margin:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;color:#172033;">
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#f4f7fb;padding:28px 0;">
      <tr>
        <td align="center">
          <table role="presentation" width="620" cellspacing="0" cellpadding="0" style="max-width:620px;width:100%;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #dde5f0;">
            <tr><td style="background:#0f5f8f;padding:26px 30px;color:#ffffff;"><div style="font-size:13px;letter-spacing:.08em;text-transform:uppercase;opacity:.85;">Tezza Lending</div><div style="font-size:25px;font-weight:700;margin-top:8px;">Repayment received</div></td></tr>
            <tr>
              <td style="padding:30px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 18px;">Hello {{firstName}}, your repayment has been received.</p>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:18px 0;">
                  <tr><td style="padding:10px 0;color:#526173;">Loan Number</td><td style="padding:10px 0;text-align:right;font-weight:700;">{{loanNumber}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Payment Amount</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Reference</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentReference}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Channel</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentChannel}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Remaining Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{remainingBalance}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">Paid at {{paidAt}}.</p>
              </td>
            </tr>
            <tr><td style="background:#f8fafc;padding:18px 30px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td></tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>', TRUE),
    ('REPAYMENT_ACKNOWLEDGEMENT', 'SMS', 'Repayment received',
     'Repayment {{repaymentAmount}} received for loan {{loanNumber}}. Ref {{repaymentReference}}. Balance {{remainingBalance}}.', TRUE),
    ('REPAYMENT_ACKNOWLEDGEMENT', 'PUSH', 'Repayment received',
     'Payment {{repaymentAmount}} received. Loan {{loanNumber}} balance {{remainingBalance}}.', TRUE);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES
    ('LOAN_FULLY_REPAID', 'EMAIL', 'Congratulations - loan {{loanNumber}} is fully repaid',
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
                  <tr><td style="padding:10px 0;color:#526173;">Customer Number</td><td style="padding:10px 0;text-align:right;font-weight:700;">{{customerNumber}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Product</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{productName}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Principal</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{principalAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Last Payment</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Total Paid</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{totalPaid}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Closing Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{remainingBalance}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Paid At</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{paidAt}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">This repayment report is generated automatically for your records.</p>
              </td>
            </tr>
            <tr><td style="background:#f8fafc;padding:18px 30px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td></tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>', TRUE),
    ('LOAN_FULLY_REPAID', 'SMS', 'Loan fully repaid',
     'Congratulations {{firstName}}, loan {{loanNumber}} is fully repaid. Total paid {{totalPaid}}.', TRUE),
    ('LOAN_FULLY_REPAID', 'PUSH', 'Loan fully repaid',
     'Congratulations. Loan {{loanNumber}} is fully repaid.', TRUE);
