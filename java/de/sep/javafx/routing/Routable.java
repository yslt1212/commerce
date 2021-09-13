package de.sep.javafx.routing;

import java.util.Map;

public interface Routable {

    public void setRouter(Router router);

    public void onRoute();

    public void onRouteWithParams(Map<String, String> params);

    public void onLeave();



}
