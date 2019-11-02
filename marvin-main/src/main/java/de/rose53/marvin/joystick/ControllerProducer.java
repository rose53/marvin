package de.rose53.marvin.joystick;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller.Type;

public class ControllerProducer {

    @Inject
    Logger logger;

    @Produces
    public Controller buildController(InjectionPoint ip) {

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        Controller ourController = null;
        for(int i = 0; i < controllers.length; i++){

            Controller controller = controllers[i];

            Type type = controller.getType();
            logger.debug("buildController: Name = >{}<, type = >{}<",controller.getName(),controller.getType());
            if (Controller.Type.STICK == type) {
                ourController = controller;
                break;
            }
        }
        return ourController;
    }
}
