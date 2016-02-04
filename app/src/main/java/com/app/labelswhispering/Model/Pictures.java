package com.app.labelswhispering.Model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Pictures")
public class Pictures extends ParseObject {

    public Pictures() {

    }


    public ParseFile getFile() {
        return getParseFile("file");
    }

    public void setFile(ParseFile file) {
        put("file", file);
    }
}
