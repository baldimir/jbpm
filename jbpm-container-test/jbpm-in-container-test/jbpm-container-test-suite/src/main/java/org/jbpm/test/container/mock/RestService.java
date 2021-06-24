/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.mock;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

import static io.undertow.Undertow.builder;
import static java.util.Collections.singletonList;

public class RestService {

    private static final String HOST = "localhost";
    private static final int PORT = 5667;
    private static final String URL = "http://" + HOST;

    public static final String ECHO_URL = URL + ":" + PORT + "/echo";
    public static final String PING_URL = URL + ":" + PORT + "/ping";
    public static final String STATUS_URL = URL + ":" + PORT + "/status";

    private static UndertowJaxrsServer server;

    private RestService() {
    }

    public static void start() {
        server = new UndertowJaxrsServer();
        server.start(builder().addHttpListener(PORT, HOST));
        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplication(new Application());
        deployment.setResources(singletonList(new Resource()));
        server.deploy(deployment);
    }

    public static void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Provider
    @Path("/")
    public static class Resource {

        @GET
        @Path("/ping")
        @Produces({"text/plain"})
        public String ping() {
            return "pong";
        }

        @POST
        @Path("/echo")
        @Consumes({"text/plain", "application/xml", "application/json"})
        @Produces({"text/plain", "application/xml", "application/json"})
        public String echo(String message) {
            return message;
        }

        @GET
        @Path("/status/{code}")
        public Response getStatus(@PathParam("code") int code) {
            if (code < 100 || code > 599) {
                code = 400;
            }
            return Response.status(code).build();
        }
    }

}
