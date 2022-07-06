package com.bitwave.cowdash.utils.language;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Translator {

    private static Translator translator;

    private final Properties properties;

    private Translator() {
        properties = new Properties();
        InputStream hintsXmlFile = Gdx.files.internal("language_files/en.xml").read();


        try {
            properties.loadFromXML(hintsXmlFile);
        } catch (InvalidPropertiesFormatException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static Translator getInstance() {
        if (translator == null) {
            translator = new Translator();
        }
        return translator;
    }

    public Array<String> getHints(String... keys) {
        Array<String> hintsArray = new Array<String>();
        for (String hint : keys) {
            try {
                hintsArray.add((String) properties.get(hint));
            } catch (Exception ex) {
            }
        }
        return hintsArray;
    }

    public String t(String key) {
        return (String) properties.get(key);
    }


}
