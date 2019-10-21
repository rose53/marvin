/*
 * Main.java
 *
 * Copyright (c) 2014, rose. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */
package de.rose53.marvin.server;

import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class Webserver {

    private Server server;

    public Webserver() {

    }

    public void start() throws Exception {

//        System.setProperty("java.naming.factory.url","org.eclipse.jetty.jndi");
//        System.setProperty("java.naming.factory.initial","org.eclipse.jetty.jndi.InitialContextFactory");


        server = new Server(8080);

        ServletContextHandler contexHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contexHandler.setContextPath("/marvin/resources");
        ServletHolder restEasyServletHolder = new ServletHolder(new HttpServletDispatcher());

        restEasyServletHolder.setInitParameter("javax.ws.rs.Application","de.rose53.marvin.server.JaxRsApplication");

        contexHandler.addServlet(restEasyServletHolder, "/*");
//        context.addEventListener(new Listener());
//        context.addEventListener(new BeanManagerResourceBindingListener());

 //       final HandlerList handlers = new HandlerList();
 //       handlers.setHandlers(new Handler[] { contextHandler });

        String  baseStr  = "/webapp";  //... contains: helloWorld.html, login.html, etc. and folder: other/xxx.html
        URL    baseUrl  = Webserver.class.getResource( baseStr );
        String  basePath = baseUrl.toExternalForm();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });

        resourceHandler.setResourceBase(basePath);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, contexHandler });
        server.setHandler(handlers);

        server.start();



//        new Resource("BeanManager", new Reference("javax.enterprise.inject.spi.BeanMnanager",
//                "org.jboss.weld.resources.ManagerObjectFactory", null));
        //server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

}