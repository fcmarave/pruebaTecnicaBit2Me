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

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class AppPrincipal {

    private static final Integer CANTIDAD = 5;
    private static final long INTERVALO = 60000; // 1 minuto
    private static final long INTERVALOEMAIL = 3600000; //1 hora
    private static String apiKey = "ee5d2031-7162-4d8e-90e8-0768615bf7a4";
    private static Pantalla pant = new Pantalla();
    private static Email email = new Email();
    private final static Logger logger = Logger.getLogger(AppPrincipal.class.getName());
    private static FileHandler fh = null;
    private static String tablaEmail;
    private static MongoCollection<Document> collection;

    public static void main(final String[] args) throws URISyntaxException, IOException, AddressException, MessagingException {

        inicializarMogoBD();
        inicializarLogs();
        inicializarEnvioEmail();
        final String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start","1"));
        params.add(new BasicNameValuePair("limit", CANTIDAD.toString()));
        params.add(new BasicNameValuePair("convert","EUR"));
        try {
        makeAPICall(uri, params);
        } catch (final IOException e) {
          logger.severe("Error: cannont access content - " + e.toString());
        } catch (final URISyntaxException e) {
          logger.severe("Error: Invalid URL " + e.toString());
        }
        while (true) {
            try {
                Thread.sleep(INTERVALO);
                makeAPICall(uri, params);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void inicializarMogoBD() {
        final MongoClient mongoClient = MongoClients.create();
            final MongoDatabase database = mongoClient.getDatabase("MongoDB");
            collection = database.getCollection("col");
    }

    public void guardarDatosBD(final Document doc) {
       collection.insertOne(doc);

       logger.info("Datos guardados en BD:");
       for (final Document cur : collection.find()) {
           logger.info(cur.toJson());
        }
    }

    private static void inicializarEnvioEmail() throws AddressException, MessagingException {
        final Thread thread = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(INTERVALOEMAIL);
                        logger.info("Intento de envio de Email en español");
                        email.enviarEmailEsp(tablaEmail);
                        logger.info("Email en español enviado satisfactoriamente!");
                        logger.info("Intento de envio de Email en ingles");
                        email.enviarEmailEng(tablaEmail);
                        logger.info("Email enviado en ingles satisfactoriamente!");
                    } catch(final InterruptedException v) {
                        System.out.println(v);
                    } catch (final AddressException e) {
                        e.printStackTrace();
                    } catch (final MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public static void inicializarLogs() {

        final SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
        try {
            fh = new FileHandler("../MyLogFile_"
                + format.format(Calendar.getInstance().getTime()) + ".log");
        } catch (final Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);

        logger.info("info msg");
        logger.severe("error message");
        logger.fine("fine message");
    }

    public static String makeAPICall(final String uri, final List<NameValuePair> parameters)
          throws URISyntaxException, IOException, AddressException, MessagingException {
        String response_content = "";

        final URIBuilder query = new URIBuilder(uri);
        query.addParameters(parameters);

        final CloseableHttpClient client = HttpClients.createDefault();
        final HttpGet request = new HttpGet(query.build());

        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader("X-CMC_PRO_API_KEY", apiKey);

        final CloseableHttpResponse response = client.execute(request);

        try {
            final HttpEntity entity = response.getEntity();
          try {
            response_content = EntityUtils.toString(entity);
        } catch (final ParseException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
          EntityUtils.consume(entity);
        } finally {
          response.close();
        }
        logger.info("response_API: " + response_content);
        pant.setPantalla(response_content, CANTIDAD);

        return response_content;
      }

    public void actualizaDatosEmail(final String message) {
        tablaEmail = message;
    }

}
