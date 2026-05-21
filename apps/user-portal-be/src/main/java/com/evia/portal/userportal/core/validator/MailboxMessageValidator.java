package com.evia.portal.userportal.core.validator;

import com.evia.portal.userportal.core.domain.Application;
import com.evia.portal.userportal.core.domain.MailboxMessage;
import com.evia.portal.userportal.core.domain.Profile;
import com.evia.portal.userportal.core.domain.enumeration.MailboxMessageStatus;
import com.evia.portal.userportal.core.util.MethodUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class MailboxMessageValidator {

  private MailboxMessageValidator() {

  }


  public static List<String> validateMailboxMessage(MailboxMessage mailboxMessage) {

    List<String> errors = new ArrayList<>();

    if (!isMailboxMessageEmpty(mailboxMessage)) {

      validateSubject(mailboxMessage.getSubject(), errors);
      validateText(mailboxMessage.getText(), errors);
      validateStatus(mailboxMessage.getStatus(), errors);
      validateSender(mailboxMessage.getSender(), errors);
      validateReceiver(mailboxMessage.getReceiver(), errors);
      validateProfile(mailboxMessage.getProfile(), errors);
      validateApplication(mailboxMessage.getApplication(), errors);
    } else {
      errors.add("The MailboxMessage is missing");
    }
    return errors;
  }

  private static void validateSubject(String subject, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(subject) || subject.length() > 255) {
      errors.add("Please fill in a valid email subject");
    }
  }

  private static void validateText(String text, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(text)) {
      errors.add("Please fill in a valid text");
    } else if (text.length() > 255) {
      errors.add("Please check the length of the text. It should be less than 255 characters.");
    }
  }

  private static void validateStatus(MailboxMessageStatus status, List<String> errors) {
    if (status==null) {
      errors.add("Please fill in a valid status");
    }
  }

  private static void validateSender(String sender, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(sender)) {
      errors.add("Please fill in a valid sender");
    }
  }

  private static void validateReceiver(String receiver, List<String> errors) {
    if (MethodUtil.isNullOrEmpty(receiver)) {
      errors.add("Please fill in a valid receiver");
    }
  }

  private static void validateProfile(Profile profile, List<String> errors) {
    if (profile==null) {
      errors.add("Please add a Profile");
    }
  }

  private static void validateApplication(Application application, List<String> errors) {
    if (application==null) {
      errors.add("Please add a Application");
    }
  }


  private static boolean isMailboxMessageEmpty(MailboxMessage mailboxMessage) {

    if (mailboxMessage==null) {
      return true;

    }
    return Stream.of(mailboxMessage.getSubject(),
      mailboxMessage.getText(),
      mailboxMessage.getStatus()).allMatch(Objects::isNull);
  }
}
