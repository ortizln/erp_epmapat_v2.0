package com.erp.emails.service;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import jakarta.mail.*;
import java.util.*;
import static com.erp.emails.service.EmailDeliveryException.Kind.*;

public final class EmailFailureClassifier {
    private EmailFailureClassifier(){}
    public static EmailDeliveryException.Kind smtp(Throwable error) {
        var queue=new ArrayDeque<Throwable>();queue.add(error);
        var seen=Collections.newSetFromMap(new IdentityHashMap<Throwable,Boolean>());
        boolean transientFailure=false,permanent=false,configuration=false,partial=false;
        while(!queue.isEmpty()) {
            var e=queue.remove();if(!seen.add(e))continue;
            if(e instanceof EmailDeliveryException d)return d.kind();
            if(e instanceof MailAuthenticationException || e instanceof AuthenticationFailedException)configuration=true;
            if(e instanceof java.net.ConnectException || e instanceof java.net.UnknownHostException)transientFailure=true;
            if(e instanceof SendFailedException s){
                if(s.getValidSentAddresses()!=null && s.getValidSentAddresses().length>0)partial=true;
                if(s.getInvalidAddresses()!=null && s.getInvalidAddresses().length>0)permanent=true;
            }
            if(e instanceof org.eclipse.angus.mail.smtp.SMTPAddressFailedException s) {
                if(s.getReturnCode()>=500)permanent=true;
                if(s.getReturnCode()>=400)transientFailure=true;
            }
            if(e instanceof org.eclipse.angus.mail.smtp.SMTPSendFailedException s) {
                if(s.getReturnCode()>=500)permanent=true;
                if(s.getReturnCode()>=400)transientFailure=true;
            }
            if(e.getCause()!=null)queue.add(e.getCause());
            if(e instanceof MessagingException m && m.getNextException()!=null)queue.add(m.getNextException());
            if(e instanceof MailSendException m)for(var nested:m.getMessageExceptions())queue.add(nested);
        }
        // EOF/timeout despues de iniciar send() no demuestra rechazo: puede haberse aceptado DATA.
        return partial?UNCERTAIN:configuration?CONFIGURATION:permanent?PERMANENT:transientFailure?TRANSIENT:UNCERTAIN;
    }
}
