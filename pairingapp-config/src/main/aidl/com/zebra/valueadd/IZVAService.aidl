/*
* Copyright (C) 2021 Zebra Technologies Corporation and/or its affiliates.
* All rights reserved.
*/
package com.zebra.valueadd;

//delegation scope for wireless connect
// delegation-zebra-wirelessconnect-api-access-configuration-service
interface IZVAService {

    /**
    * Method which we will receive the json string from third application as a input
    * If data is valid, returns success otherwise returns failure as a output. Caller should call this API just after
    * bind to the service.
    */
    String processZVARequest(in String json);
}