/*
 * Copyright © 2016 serngawy and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.example.impl;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.opendaylight.aaa.cert.api.IAaaCertProvider;
import org.opendaylight.aaa.cert.impl.AaaCertMdsalProvider;
import org.opendaylight.aaa.cert.impl.AaaCertProvider;
import org.opendaylight.aaa.cert.impl.DefaultMdsalSslData;
import org.opendaylight.aaa.cert.impl.KeyStoreConstant;
import org.opendaylight.aaa.encrypt.AAAEncryptionService;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.example.api.ICertManagerConsumer;
import org.opendaylight.yang.gen.v1.urn.opendaylight.yang.aaa.cert.rev151126.AaaCertServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertManagerConsumerProvider implements ICertManagerConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(CertManagerConsumerProvider.class);

    private final DataBroker dataBroker;
    private final IAaaCertProvider aaaCertProvider;

    public CertManagerConsumerProvider(final AaaCertServiceConfig aaaCertServiceConfig, final DataBroker dataBroker,  final AAAEncryptionService encryptionSrv) {
        this.dataBroker = dataBroker;
        LOG.info("we r in");
        if (aaaCertServiceConfig.isUseConfig()) {
            if (aaaCertServiceConfig.isUseMdsal()) {
                aaaCertProvider = new DefaultMdsalSslData(new AaaCertMdsalProvider(dataBroker, encryptionSrv), aaaCertServiceConfig.getBundleName(),
                        aaaCertServiceConfig.getCtlKeystore(), aaaCertServiceConfig.getTrustKeystore());
                LOG.debug("Using the AaaCertManagerConsumer SslData as aaaCertProvider");
            } else {
                aaaCertProvider = new AaaCertProvider(aaaCertServiceConfig.getCtlKeystore(), aaaCertServiceConfig.getTrustKeystore());
                LOG.debug("Using the AaaCertManagerConsumer keystore files as aaaCertProvider");
            }
            aaaCertProvider.createKeyStores();
            LOG.info("AaaCertManagerConsumerProvider has been initialized");
        } else {
            aaaCertProvider = null;
            LOG.info("AaaCertManagerConsumerProvider has not been initialized, change the initial config data and restart Opendaylight");
        }
    }

    public void init() {
        LOG.info("AaaCertManagerConsumerProvider Session Initiated");
    }

    public void close() {
        LOG.info("AaaCertManagerConsumerProvider Closed");
    }

    @Override
    public KeyStore getODLKeyStore() {
        return aaaCertProvider.getODLKeyStore();
    }

    @Override
    public KeyStore getTrustKeyStore() {
        return aaaCertProvider.getTrustKeyStore();
    }

    @Override
    public String[] getCipherSuites() {
        return aaaCertProvider.getCipherSuites();
    }

    @Override
    public String getCertificateTrustStore(String storePasswd, String alias, boolean withTag) {
        return aaaCertProvider.getCertificateTrustStore(storePasswd, alias, withTag);
    }

    @Override
    public String getODLKeyStoreCertificate(String storePasswd, boolean withTag) {
        return aaaCertProvider.getODLKeyStoreCertificate(storePasswd, withTag);
    }

    @Override
    public String genODLKeyStoreCertificateReq(String storePasswd, boolean withTag) {
        return aaaCertProvider.genODLKeyStoreCertificateReq(storePasswd, withTag);
    }

    @Override
    public SSLContext getServerContext() {
        String algorithm = Security
                .getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        SSLContext serverContext = null;
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(aaaCertProvider.getODLKeyStore(), aaaCertProvider.getOdlKeyStoreInfo().getStorePassword().toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(aaaCertProvider.getTrustKeyStore());

            serverContext = SSLContext.getInstance(KeyStoreConstant.TLS_PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (final NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException  e) {
            LOG.error("Error while creating SSLContext ", e);
        }
        return serverContext;
    }

    @Override
    public String getODLKeyStoreCertificate() {
        return aaaCertProvider.getODLKeyStoreCertificate(true);
    }
}