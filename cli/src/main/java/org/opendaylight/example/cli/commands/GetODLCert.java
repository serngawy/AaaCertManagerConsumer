/*
 * Copyright (c) 2016 serngawy Technologies. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.example.cli.commands;

import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.opendaylight.example.api.ICertManagerConsumer;

/**
 * GetODLCert get the ODL key store certificate.
 *
 * @author serngawy
 *
 */

@Command(name = "get-cmc-odl-cert", scope = "cmc", description = "get the opendaylight controller Certificate.")
public class GetODLCert extends OsgiCommandSupport{

    protected ICertManagerConsumer certProvider;

    public GetODLCert(final ICertManagerConsumer aaaCertProvider) {
        this.certProvider = aaaCertProvider;
    }

    @Override
    protected Object doExecute() throws Exception {
        return certProvider.getODLKeyStoreCertificate();
    }

}
