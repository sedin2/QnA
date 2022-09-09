package com.sedin.qna.util;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {

    static Attributes.Attribute getBornDateFormat() {
        return key("format").value("yyyy-MM-dd");
    }

    static Attributes.Attribute getDateFormat() {
        return key("format").value("yyyy-MM-dd HH:mm:ss");
    }
}
