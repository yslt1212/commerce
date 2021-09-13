package de.sep.javafx.routing;

import javafx.scene.Scene;

public class Route {

    private String route;
    private Scene scene;
    private Routable routable;
    private boolean wholeScene;

    public Route(String route, Scene scene, boolean wholeScene) {
        this.route = route;
        this.scene = scene;
        this.routable = null;
        this.wholeScene = wholeScene;
    }

    public Route(String route, Scene scene, Routable routable, boolean wholeScene) {
        this.route = route;
        this.scene = scene;
        this.routable = routable;
        this.wholeScene = wholeScene;
    }

    public String getRoute() {

        return route;
    }

    public void setRoute(String route) {

        this.route = route;
    }

    public Routable getRoutable() {
        return routable;
    }

    public void setRoutable(Routable routable) {
        this.routable = routable;
    }

    public Scene getScene() {

        return scene;
    }

    public void setScene(Scene scene) {

        this.scene = scene;
    }

    public boolean isWholeScene() {

        return wholeScene;
    }

    public void setWholeScene(boolean wholeScene) {

        this.wholeScene = wholeScene;
    }
}
