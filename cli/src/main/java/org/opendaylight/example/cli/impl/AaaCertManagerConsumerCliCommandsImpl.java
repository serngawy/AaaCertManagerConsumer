/*
 * Copyright © 2016 serngawy and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.example.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.example.cli.api.AaaCertManagerConsumerCliCommands;

public class AaaCertManagerConsumerCliCommandsImpl implements AaaCertManagerConsumerCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(AaaCertManagerConsumerCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public AaaCertManagerConsumerCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("AaaCertManagerConsumerCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}