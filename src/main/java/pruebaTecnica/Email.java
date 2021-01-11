/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package pruebaTecnica;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
    final Properties props = new Properties();
    private static Session session;
    final static String username = "USERNAME@gmail.com";
    final static String password = "PASSWORD";
    private static final String cabeceraEsp = "Hola, este es un mensaje automático de información actualiada de sus criptomonedas favoritas. \n\n";
    private static final String cabeceraEng = "Hello, this is an automatic message of updated information about your favorite cryptocurrencies. \n\n";
    private static final String EMAILTO = "people@bit2me.com";

    Email () {
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    void enviarEmailEsp(final String texto) throws AddressException, MessagingException {

        final MimeMessage message = new MimeMessage(session);

        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(EMAILTO));
        message.setSubject("Info Criptomonedas");
        message.setText(cabeceraEsp + texto);

        Transport.send(message);
    }

    void enviarEmailEng(final String texto) throws AddressException, MessagingException {

        final MimeMessage message = new MimeMessage(session);

        message.addRecipient(Message.RecipientType.TO,
                new InternetAddress(EMAILTO));
        message.setSubject("Info Cryptocurrencies");
        message.setText(cabeceraEng + texto);

        Transport.send(message);
    }
}
