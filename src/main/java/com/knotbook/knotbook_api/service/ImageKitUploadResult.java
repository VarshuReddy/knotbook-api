package com.knotbook.knotbook_api.service;



public record ImageKitUploadResult(java.util.Optional<String> fileId,
                                   java.util.Optional<String> url,
                                   java.util.Optional<String> filePath
) {
}
