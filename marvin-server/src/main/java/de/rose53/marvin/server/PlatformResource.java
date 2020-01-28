/*
 * Resource.java
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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import de.rose53.marvin.RestHelper;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class PlatformResource {

    @Inject
    Logger logger;

    @PUT
    @Path("/mecanum")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setMecanumDrive(@FormParam("ch1") byte ch1, @FormParam("ch3") byte ch3, @FormParam("ch4") byte ch4) {
        RestHelper.getMecanumDrive().mecanumDrive(ch1, ch3, ch4);
    }

    @GET
    @Path("/mecanum/current")
    public short[] getCurrent() {
        return RestHelper.getMecanumDrive().getCurrent();
    }

    @GET
    @Path("/heading")
    public Response getHeading() {

        Float heading = RestHelper.getCompass().getHeading();
        if (heading == null) {
            return Response.noContent().build();
        }
        return Response.ok(heading).build();
    }

    @PUT
    @Path("/pan")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setPan(@FormParam("pan") short pan) {
        RestHelper.getPanTiltSensor().setPan(pan);
    }

    @PUT
    @Path("/tilt")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setTilt(@FormParam("tilt") short tilt) {
        RestHelper.getPanTiltSensor().setTilt(tilt);
    }

    @PUT
    @Path("/pantilt")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setPanTilt(@FormParam("pan") short pan, @FormParam("tilt") short tilt) {
        RestHelper.getPanTiltSensor().setPanTilt(pan,tilt);
    }
}