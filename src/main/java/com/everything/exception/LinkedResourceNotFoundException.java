/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.everything.exception;

public class LinkedResourceNotFoundException extends RuntimeException {

    private final String missingId;

    public LinkedResourceNotFoundException(String missingId) {
        super("Linked resource not found: roomId '" + missingId + "' does not exist");
        this.missingId = missingId;
    }

    public String getMissingId() { return missingId; }
}