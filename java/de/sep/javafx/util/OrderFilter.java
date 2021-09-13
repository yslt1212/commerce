package de.sep.javafx.util;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilter {


    public enum SortByDate{
        BY_ONGOING,
        BY_PAST

    }
    SortByDate sortByDate = SortByDate.BY_ONGOING;

}
