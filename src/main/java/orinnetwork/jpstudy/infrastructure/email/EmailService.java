package orinnetwork.jpstudy.infrastructure.email;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.SyncPoller;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailClient emailClient;

    @Value("${spring.cloud.azure.communication.email.sender-address}")
    private String senderAddress;

    public void sendPasswordResetLink(String toEmail, String resetToken) {

        String resetLink = "https://jpstudy.org/reset-password?token=" + resetToken;
        String subject = "[jpstudy] 비밀번호 재설정 요청";

        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>비밀번호 재설정</title>
                </head>
                <body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', '맑은 고딕', sans-serif;">
                
                    <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                
                                <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05);">
                
                                    <tr>
                                        <td style="padding: 40px 30px 30px 30px; text-align: center;">
                                            <h1 style="margin: 0; font-size: 26px; font-weight: bold; color: #333333;">
                                                비밀번호 재설정 요청
                                            </h1>
                                            <p style="margin: 20px 0 30px 0; font-size: 16px; color: #555555; line-height: 1.6;">
                                                아래 버튼을 클릭하여 비밀번호를 재설정하세요.
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td align="center" style="padding: 0 30px 40px 30px;">
                                            <a href="%s" target="_blank" style="display: inline-block; padding: 15px 35px; font-size: 18px; font-weight: bold; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 5px;">
                                                비밀번호 재설정하기
                                            </a>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding: 0 30px 40px 30px; text-align: center; font-size: 14px; color: #888888; line-height: 1.5;">
                                            <p style="margin: 0;">
                                                이 링크는 <strong>15분</strong>간 유효합니다.
                                            </p>
                                            <p style="margin: 10px 0 0 0;">
                                                만약 이 요청을 직접 하지 않으셨다면, 이 이메일을 무시해 주세요.
                                            </p>
                
                                            <p style="margin: 20px 0 0 0; font-size: 12px; color: #bbbbbb; word-break: break-all;">
                                                버튼이 작동하지 않으면 이 링크를 복사하여 붙여넣으세요:<br> %s
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding: 30px; background-color: #f9f9f9; text-align: center; font-size: 12px; color: #aaaaaa;">
                                            <p style="margin: 0;">
                                                © 2025 jpstudy. All rights reserved.
                                            </p>
                                        </td>
                                    </tr>
                
                                </table>
                
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """;

        String htmlContent = String.format(htmlTemplate, resetLink, resetLink);

        String plainTextContent = "비밀번호 재설정 링크: " + resetLink;

        send(toEmail, subject, plainTextContent, htmlContent);
    }

    private void send(String toEmail, String subject, String plainText, String html) {

        EmailAddress toRecipient = new EmailAddress(toEmail);

        EmailMessage emailMessage = new EmailMessage()
                .setSenderAddress(senderAddress)
                .setToRecipients(Collections.singletonList(toRecipient))
                .setSubject(subject)
                .setBodyPlainText(plainText)
                .setBodyHtml(html);

        try {
            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage);
            EmailSendResult result = poller.waitForCompletion().getValue();
            log.info("Email sent to {}. Operation Id: {}", toEmail, result.getId());
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }
}
