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

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pantalla {

    JFrame f;
    AppPrincipal main;
    Pantalla(final String result, final Integer cantidad) throws AddressException, MessagingException {

        final JScrollPane sp = crearPane(result, cantidad);
        f.add(sp);
        f.setSize(300,180);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Pantalla() {
        f=new JFrame();
        main = new AppPrincipal();
    }

    private JScrollPane crearPane(final String result, final Integer cantidad) throws AddressException, MessagingException {
        final JSONObject jSon = new JSONObject(result);
        final JSONArray dataRes = jSon.getJSONArray("data");
        JSONObject oj = null;
        Float price;
        final String[][] datos = new String[cantidad][2];
        String message = "";
        for (int i = 0; i < cantidad; i++) {
            oj = dataRes.getJSONObject(i);
            price = oj.getJSONObject("quote").getJSONObject("EUR").getFloat("price");
            datos[i][0] = oj.getString("name");
            datos[i][1] = price.toString();
            message += oj.getString("name") + ": " + price.toString() + "\n";
            final Document doc = new Document("name", "MongoDB")
                    .append("name", oj.getString("name"))
                    .append("price", price);
            main.guardarDatosBD(doc);
        }
        f=new JFrame();
        final String column[]={"MONEDA","PRECIO"};
        final JTable jt=new JTable(datos,column);
        jt.setBounds(30,40,200,300);
        final JScrollPane sp=new JScrollPane(jt);
        main.actualizaDatosEmail(message);
        return sp;
    }
    public void setPantalla(final String result, final Integer cantidad) throws AddressException, MessagingException {
        f.setVisible(false);
        f.removeAll();
        anyadirJPane(result, cantidad);
    }

    private void anyadirJPane(final String result, final Integer cantidad) throws AddressException, MessagingException {
        final JScrollPane sp = crearPane(result, cantidad);
        f.add(sp);
        f.setSize(300,180);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
