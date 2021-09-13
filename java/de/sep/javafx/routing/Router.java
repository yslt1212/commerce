package de.sep.javafx.routing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.sep.javafx.util.Logger;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// class which "routes" inside of our Application
public class Router {
    Map<String, Route> routes = new HashMap<>();
    List<Route> routeHistory = new LinkedList<>();
    Stage stage;
    Logger logger = new Logger(getClass().getCanonicalName());

    /*
        Takes the stage and all scenes which the router should route to as arguments
        We cant instantiate the scenes new inside of here, because then they would not have
        the Router cause they also instantiate a new Controller
    */
    public Router(Stage stage){
        this.stage = stage;
    }

    public void addRoute(Route route) {
        routes.put(route.getRoute(), route);
    }

    private Route getRoute(String route){
        return routes.get(route);
    }

    // routes by setting the scene of the given stage
    public void route(String route) {
        Route wantedRoute = getRoute(route);
        if(wantedRoute != null) {
            leaveRoute();
            prepareRoute(wantedRoute);
            routeTo(wantedRoute, true);
        }else{
            logger.log("U routed to an undefined route", "Maybe you forgot to initiate the route or to add it to the router");
        }
    }

    // routes by setting the scene of the given stage with given parameters
    public void route(String route, Map<String, String> params) {
        Route wantedRoute = getRoute(route);
        if(wantedRoute != null) {
            leaveRoute();
            prepareRoute(wantedRoute, params);
            routeTo(wantedRoute, true);
        }else{
            logger.log("U routed to an undefined route", "Maybe you forgot to initiate the route or to add it to the router");
        }
    }

    public void back(){
        leaveRoute();
        //Removing current site from route history
        routeHistory.remove(routeHistory.size()-1);
        //Getting previous site from route history and route to it
        Route route = routeHistory.get(routeHistory.size()-1);

        prepareRoute(route);
        routeTo(route,false);
    }

    private void prepareRoute(Route route){
        Routable routable;
        if((routable = route.getRoutable()) != null){

            routable.onRoute();
        }
    }

    private void leaveRoute() {
        Routable routable;
        if(!routeHistory.isEmpty()) {
            Route route = routeHistory.get(routeHistory.size() -1);
            if((routable = route.getRoutable()) != null){
                routable.onLeave();
            }
        }

    }

    private void prepareRoute(Route route, Map<String, String> params){
        Routable routable;
        if((routable = route.getRoutable()) != null){
            routable.onRouteWithParams(params);
        }
    }

    private void routeTo(Route route,boolean history){
        Scene scene = route.getScene();
        if (route.isWholeScene()) {
            /*
            Since we dont want to change the whole Home Screen on clicks on the navigation
            We got an booleanValue to determine if we want to change the whole screen
            Or just parts of it
            */
            stage.setScene(scene);
        } else {
            BorderPane bp = (BorderPane) stage.getScene().lookup("#navigationBorderPane");
            if (bp == null) {
                logger.log( "You are trying to route to a Route which does not have a BorderPane with the id "
                        + "#navigationBorderPane.");

            } else {
                Node center = route.getScene().lookup("#routerParent");
                if(center != null) {
                    bp.setCenter(center);
                    if (history) {
                        routeHistory.add(route);
                    }
                }else{
                    logger.log( "It seems like something went wrong looking up #routerParent.\"\n" +
                            "+ \"Did you forget to define the id of your root pane in FXML.");
                }
            }
        }

    }

    public void clearRoutingHistory() {
        this.routeHistory.clear();
    }


}
