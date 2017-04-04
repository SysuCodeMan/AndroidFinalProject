package com.example.john.finalproject.Life.express;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Express implements Serializable{
    private String name;
    private String logoUrl;
    private String officialUrl;

    public List<Content> content = new ArrayList<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl) {
        this.officialUrl = officialUrl;
    }


    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Express{" +
                "name='" + name + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", officialUrl='" + officialUrl + '\'' +
                ", content=" + content +
                '}';
    }
}
