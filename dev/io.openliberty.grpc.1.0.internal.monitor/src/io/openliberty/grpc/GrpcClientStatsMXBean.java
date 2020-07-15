/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.grpc;

/**
 * Management interface for MBeans with names of the form "WebSphere:type=GrpcClientStats,name=*"
 * where * is the name of a gRPC service.
 * 
 * TODO how can we query gRPC client-side metrics?
 * 
 * @ibm-api
 */
public interface GrpcClientStatsMXBean {
    /**
     * Retrieves the total number of RPCs started on the server.
     * 
     * @return the total number of RPCs started on the server
     */
    public long getRpcStartedCount();

    /**
     * Retrieves the total number of RPCs completed on the server,
     * 
     * @return the total number of RPCs completed on the server
     */
    public long getRpcCompletedCount();

    /**
     * Retrieves the total number of stream messages that the server has received for this gRPC service.
     * 
     * @return the total number of stream messages received for this service
     */
    public long getReceivedMessagesCount();

    /**
     * Retrieves the total number of stream messages sent by this gRPC service.
     * 
     * @return the total number of stream messages sent by this service
     */
    public long getSentMessagesCount();
}