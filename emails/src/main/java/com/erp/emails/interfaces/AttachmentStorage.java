package com.erp.emails.interfaces;

public interface AttachmentStorage {
    StoredObject save(String filename, String contentType, byte[] bytes);
    record StoredObject(String storageRef, long size, String sha256) {}
}