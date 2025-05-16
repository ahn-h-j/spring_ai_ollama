package com.example.demo.langgraph;


import java.util.List;

public class DocumentsRequest {
    private List<CafeInfo> documents;

    public List<CafeInfo> getDocuments() {
        return documents;
    }

    public void setDocuments(List<CafeInfo> documents) {
        this.documents = documents;
    }

    public static class CafeInfo {
        private String name;
        private String text;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}