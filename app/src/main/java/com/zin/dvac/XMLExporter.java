package com.zin.dvac;

import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

public class XMLExporter {

    public static void exportToXML(DatabaseHelper dbHelper, OutputStream outputStream) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(outputStream, "UTF-8");
            serializer.startDocument(null, Boolean.TRUE);
            serializer.startTag(null, "passwords");

            List<Password> passwordList = dbHelper.getAllPasswords();

            for (Password password : passwordList) {
                exportPassword(serializer, password);
            }

            serializer.endTag(null, "passwords");
            serializer.endDocument();
        } finally {
            outputStream.close();
        }
    }

    public static String exportToXMLString(DatabaseHelper dbHelper) throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument(null, Boolean.TRUE);
            serializer.startTag(null, "passwords");

            List<Password> passwordList = dbHelper.getAllPasswords();

            for (Password password : passwordList) {
                exportPassword(serializer, password);
            }

            serializer.endTag(null, "passwords");
            serializer.endDocument();

            return writer.toString();
        } finally {
            writer.close();
        }
    }

    private static void exportPassword(XmlSerializer serializer, Password password) throws IOException {
        serializer.startTag(null, "password");

        serializer.startTag(null, "id");
        serializer.text(String.valueOf(password.getId()));
        serializer.endTag(null, "id");

        serializer.startTag(null, "username");
        serializer.text(password.getUsername());
        serializer.endTag(null, "username");

        serializer.startTag(null, "passwordValue");
        serializer.text(password.getPassword());
        serializer.endTag(null, "passwordValue");

        serializer.startTag(null, "description");
        serializer.text(password.getDescription());
        serializer.endTag(null, "description");

        serializer.endTag(null, "password");
    }
}
