package com.akine_api.application.dto.command;

public record CreateSubscriptionCommand(
        String ownerFirstName,
        String ownerLastName,
        String ownerDocumentoFiscal,
        String ownerEmail,
        String ownerPhone,
        String ownerPassword,
        String companyName,
        String companyCuit,
        String companyAddress,
        String companyCity,
        String companyProvince,
        String consultorioName,
        String consultorioAddress,
        String consultorioPhone,
        String consultorioEmail
) {}
