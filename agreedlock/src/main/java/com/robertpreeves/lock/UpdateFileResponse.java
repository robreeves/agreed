package com.robertpreeves.lock;

public class UpdateFileResponse {
    public final String filename;
    public final String content;

    public UpdateFileResponse(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }
}
