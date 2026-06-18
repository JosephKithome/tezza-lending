UPDATE notification_templates
SET active = FALSE
WHERE event_type = 'REPAYMENT_ACKNOWLEDGEMENT'
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
            <tr>
              <td style="background:#0f5f8f;padding:28px 32px;color:#ffffff;">
                <div style="font-size:13px;letter-spacing:.08em;text-transform:uppercase;opacity:.9;">Tezza Lending</div>
                <div style="font-size:26px;font-weight:700;margin-top:8px;">Repayment receipt</div>
                <div style="font-size:14px;margin-top:8px;opacity:.9;">Loan {{loanNumber}}</div>
              </td>
            </tr>
            <tr>
              <td style="padding:30px 32px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 20px;">Hello {{firstName}}, we have received your repayment. Below are the loan and payment details for your records.</p>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:0 0 22px;">
                  <tr>
                    <td colspan="2" style="padding:0 0 10px;font-size:13px;color:#0f5f8f;font-weight:700;text-transform:uppercase;letter-spacing:.06em;">Loan details</td>
                  </tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Customer Number</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{customerNumber}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Product</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{productName}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Structure</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{structure}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Principal</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{principalAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Due Date</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{dueDate}}</td></tr>
                </table>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:0 0 22px;">
                  <tr>
                    <td colspan="2" style="padding:0 0 10px;font-size:13px;color:#0f5f8f;font-weight:700;text-transform:uppercase;letter-spacing:.06em;">Payment details</td>
                  </tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Payment Amount</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{repaymentAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Previous Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{previousBalance}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Remaining Balance</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;color:#0f5f8f;">{{remainingBalance}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Total Paid</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{totalPaid}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Channel</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentChannel}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Reference</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{repaymentReference}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Paid At</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{paidAt}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">Keep this repayment receipt for your records.</p>
              </td>
            </tr>
            <tr>
              <td style="background:#f8fafc;padding:18px 32px;font-size:12px;color:#6b7788;">This is an automated notification from Tezza Lending.</td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>',
    TRUE
);
