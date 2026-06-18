INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'LOAN_DISBURSED',
       'EMAIL',
       'Loan {{loanNumber}} has been disbursed',
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
                <div style="font-size:25px;font-weight:700;margin-top:8px;">Loan disbursed</div>
              </td>
            </tr>
            <tr>
              <td style="padding:30px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 18px;">Hello {{firstName}}, your loan has been disbursed successfully.</p>
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="border-collapse:collapse;margin:18px 0;">
                  <tr><td style="padding:10px 0;color:#526173;">Loan Number</td><td style="padding:10px 0;text-align:right;font-weight:700;">{{loanNumber}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Product</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{productName}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Principal</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{principalAmount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Amount Due</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;font-weight:700;">{{amount}}</td></tr>
                  <tr><td style="padding:10px 0;color:#526173;border-top:1px solid #edf1f6;">Due Date</td><td style="padding:10px 0;text-align:right;border-top:1px solid #edf1f6;">{{dueDate}}</td></tr>
                </table>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">Please keep this message for your records.</p>
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
    WHERE event_type = 'LOAN_DISBURSED' AND channel = 'EMAIL' AND active = TRUE
);

INSERT INTO notification_templates (event_type, channel, subject, body, active)
SELECT 'LOAN_DISBURSED',
       'SMS',
       'Loan disbursed',
       'Hello {{firstName}}, loan {{loanNumber}} has been disbursed. Amount due {{amount}} by {{dueDate}}.',
       TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM notification_templates
    WHERE event_type = 'LOAN_DISBURSED' AND channel = 'SMS' AND active = TRUE
);
