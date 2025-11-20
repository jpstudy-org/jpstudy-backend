package orinnetwork.jpstudy.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessage {

    SIGNUP_WELCOME("notification.signup.welcome"), // 키값
    EXAM_SUBMITTED("notification.exam.submitted"),
    LEVEL_UP("notification.member.levelup"),
    INQUIRY_ANSWERED("notification.inquiry.answered");

    private final String key;
}