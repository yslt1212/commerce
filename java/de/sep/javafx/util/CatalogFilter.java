package de.sep.javafx.util;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CatalogFilter {

    String query = "";
    String category = "";

    public enum SortingByPrice{
        NONE,
        BY_PRICE_ASC,
        BY_PRICE_DESC
    }
    SortingByPrice sortingByPrice = SortingByPrice.NONE;

    public enum SortingByOrder{
        BY_ALL_ORDERS,
        BY_AUCTIONS,
        BY_PRODUCTS,
        BY_EXPIRED_ORDERS
    }
    SortingByOrder sortingByOrder = SortingByOrder.BY_ALL_ORDERS;

    public enum SortingByDistance{
        NONE,
        BY_DISTANCE_ASC,
        BY_DISTANCE_DESC
    }
    SortingByDistance sortingByDistance = SortingByDistance.NONE;


}
