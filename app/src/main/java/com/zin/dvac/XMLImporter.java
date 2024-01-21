package com.zin.dvac;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class XMLImporter {

    public static void importFromXML(DatabaseHelper dbHelper, InputStream inputStream) throws IOException {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStream, null);
            parser.nextTag(); // Skip to <passwords> tag

            parser.require(XmlPullParser.START_TAG, null, "passwords");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String tagName = parser.getName();

                if ("password".equals(tagName)) {
                    Password password = parsePassword(parser);
                    dbHelper.addPassword(password);
                } else {
                    skip(parser); // Skip unknown tags
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    private static Password parsePassword(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "password");
        long id = -1;
        String username = null;
        String passwordValue = null;
        String description = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tagName = parser.getName();

            switch (tagName) {
                case "id":
                    id = Long.parseLong(readText(parser));
                    break;
                case "username":
                    username = readText(parser);
                    break;
                case "passwordValue":
                    passwordValue = readText(parser);
                    break;
                case "description":
                    description = readText(parser);
                    break;
                default:
                    skip(parser); // Skip unknown tags
                    break;
            }
        }

        return new Password(id, username, passwordValue, description);
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws IOException, XmlPullParserException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
