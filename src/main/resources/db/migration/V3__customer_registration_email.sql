ALTER TABLE customers ADD COLUMN IF NOT EXISTS registration_otp VARCHAR(10);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS registration_otp_expires_at TIMESTAMP;

INSERT INTO notification_templates (event_type, channel, subject, body, active)
VALUES (
    'CUSTOMER_REGISTERED',
    'EMAIL',
    'Welcome to Tezza Lending - verify your account',
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
                <div style="font-size:26px;font-weight:700;margin-top:8px;">Welcome, {{firstName}}</div>
              </td>
            </tr>
            <tr>
              <td style="padding:30px;">
                <p style="font-size:16px;line-height:1.6;margin:0 0 18px;">Your customer profile has been created successfully.</p>
                <p style="font-size:15px;line-height:1.6;margin:0 0 18px;">Customer Number: <strong>{{customerNumber}}</strong></p>
                <div style="background:#eef6fb;border:1px solid #cfe4f2;border-radius:10px;padding:22px;text-align:center;margin:24px 0;">
                  <div style="font-size:13px;color:#526173;text-transform:uppercase;letter-spacing:.08em;">Verification OTP</div>
                  <div style="font-size:34px;font-weight:700;letter-spacing:8px;color:#0f5f8f;margin-top:8px;">{{otp}}</div>
                  <div style="font-size:13px;color:#526173;margin-top:10px;">This code expires in 10 minutes.</div>
                </div>
                <p style="font-size:14px;line-height:1.6;color:#526173;margin:0;">If you did not request this account, ignore this email or contact support.</p>
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
);
