/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

import com.everything.exception.GlobalExceptionMapper;
import com.everything.exception.LinkedResourceNotFoundExceptionMapper;
import com.everything.exception.RoomNotEmptyExceptionMapper;
import com.everything.exception.SensorUnavailableExceptionMapper;
import com.everything.filter.LoggingFilter;

import com.smartcampus.resources.DiscoveryResource;
import com.smartcampus.resources.RoomResource;
import com.smartcampus.resources.SensorResource;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static final String BASE_URI = "http://localhost:8081/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig();

        rc.register(DiscoveryResource.class);
        rc.register(RoomResource.class);
        rc.register(SensorResource.class);

        rc.register(RoomNotEmptyExceptionMapper.class);
        rc.register(LinkedResourceNotFoundExceptionMapper.class);
        rc.register(SensorUnavailableExceptionMapper.class);
        rc.register(GlobalExceptionMapper.class);
        rc.register(LoggingFilter.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        LOGGER.info("Smart Campus API started at " + BASE_URI);
        LOGGER.info("Press ENTER to stop the server...");
        System.in.read();
        server.stop();
    }
}
