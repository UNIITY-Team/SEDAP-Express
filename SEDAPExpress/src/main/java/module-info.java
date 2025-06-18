/**
 * Note: This license has also been called the “Simplified BSD License” and the “FreeBSD License”.
 *
 * Copyright 2024-2025 UNIITY POC: Volker Voß, Federal Armed Forces of Germany
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSEnARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
module sedapexpress {

    exports de.bundeswehr.uniity.sedapexpress;
    exports de.bundeswehr.uniity.sedapexpress.controls;
    exports de.bundeswehr.uniity.sedapexpress.messages;
    exports de.bundeswehr.uniity.sedapexpress.network;
    exports de.bundeswehr.uniity.sedapexpress.processing;
    exports de.bundeswehr.uniity.sedapexpress.utils;
    exports de.bundeswehr.uniity.sedapexpress.json;

    opens de.bundeswehr.uniity.sedapexpress;
    opens de.bundeswehr.uniity.sedapexpress.controls;
    opens de.bundeswehr.uniity.sedapexpress.messages;
    opens de.bundeswehr.uniity.sedapexpress.network;
    opens de.bundeswehr.uniity.sedapexpress.processing;
    opens de.bundeswehr.uniity.sedapexpress.utils;
    opens de.bundeswehr.uniity.sedapexpress.json;

    requires java.logging;

    requires transitive org.bouncycastle.util;
    requires transitive org.bouncycastle.provider;
    requires transitive org.bouncycastle.pkix;

    requires transitive org.eclipse.paho.mqttv5.client;

    requires transitive jssc;

    requires transitive com.google.gson;

    requires transitive com.google.protobuf;
    requires transitive com.google.protobuf.util;

    requires transitive javafx.base;
    requires transitive javafx.fxml;
    requires transitive javafx.controls;
    requires transitive jdk.httpserver;
    requires transitive java.net.http;
}