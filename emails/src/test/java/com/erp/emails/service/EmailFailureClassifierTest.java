package com.erp.emails.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.erp.emails.service.EmailDeliveryException.Kind.*;
class EmailFailureClassifierTest {
    @Test void authenticationIsAConfigurationFailure(){assertEquals(CONFIGURATION,EmailFailureClassifier.smtp(new org.springframework.mail.MailAuthenticationException("535")));}
    @Test void connectionRefusalIsSafeToRetry(){assertEquals(TRANSIENT,EmailFailureClassifier.smtp(new RuntimeException(new java.net.ConnectException("refused"))));}
    @Test void timeoutOrEofDuringHandoffIsUncertain(){assertEquals(UNCERTAIN,EmailFailureClassifier.smtp(new java.net.SocketTimeoutException("DATA")));}
    @Test void invalidAddressIsPermanent(){assertEquals(PERMANENT,EmailFailureClassifier.smtp(new jakarta.mail.SendFailedException("invalid",null,null,null,new jakarta.mail.Address[]{new jakarta.mail.internet.InternetAddress()})));}

    @Test void partialDeliveryMustBeConciliatedBeforeRetry(){
        var sent=new jakarta.mail.Address[]{new jakarta.mail.internet.InternetAddress()};
        var invalid=new jakarta.mail.Address[]{new jakarta.mail.internet.InternetAddress()};
        assertEquals(UNCERTAIN,EmailFailureClassifier.smtp(new jakarta.mail.SendFailedException("partial",null,sent,null,invalid)));
    }
}
