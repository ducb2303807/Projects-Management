package com.group4.projects_management_fe.core.api.ultils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class MultipartHelper {
    public static MultipartBody toMultipartBody(ObjectMapper jsonMapper, Object dto, Map<String, List<File>> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = jsonMapper.convertValue(dto, Map.class);
            fields.forEach((key, value) -> {
                if (value != null) builder.addFormDataPart(key, value.toString());
            });
        } catch (Exception e) { e.printStackTrace(); }

        // xử lý file
        if (files != null) {
            files.forEach((fieldName, fileList) -> {
                for (File file : fileList) {
                    if (file != null && file.exists()) {
                        // lấy MediaType (png, pdf, jpg...)
                        String contentType = URLConnection.guessContentTypeFromName(file.getName());
                        MediaType mediaType = MediaType.parse(contentType != null ? contentType : "application/octet-stream");

                        RequestBody fileBody = RequestBody.create(file, mediaType);
                        builder.addFormDataPart(fieldName, file.getName(), fileBody);
                    }
                }
            });
        }

        return builder.build();
    }
}
