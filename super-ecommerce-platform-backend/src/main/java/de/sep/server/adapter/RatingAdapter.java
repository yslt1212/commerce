package de.sep.server.adapter;

import de.sep.domain.dto.AddRatingDto;
import de.sep.domain.dto.GetRatingDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingAdapter extends DataAdapter{



    public RatingAdapter() throws SQLException {
        super("rating");
    }

    public void addRating(AddRatingDto rdto){

        try {
            String intoOfferBaseQuery = "INSERT INTO RATING (user_id, rated_user_id,rating_numberic, rating_text) VALUES(";
            StringBuilder queryBuilder = new StringBuilder(intoOfferBaseQuery);

            queryBuilder.append(String.format("'%d','%d','%d','%s');",
                    rdto.getUserId(),
                    rdto.getRatedUserId(),
                    rdto.getRating(),
                    rdto.getComment()));


            String query = queryBuilder.toString();

            addQuery(query);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public List<GetRatingDto> getRating(int Rated_UserId) throws SQLException {

        List<GetRatingDto> ratings=new ArrayList<>();
        GetRatingDto grdto = null;
        ResultSet queryResult = super.getQuery("SELECT rating.*, user.username FROM rating natural JOIN user where rated_user_id="+Rated_UserId);

        if(queryResult.next()) {
            do {
                grdto = new GetRatingDto(
                        queryResult.getInt("rating_id"),
                        queryResult.getInt("rated_user_id"),
                        queryResult.getInt("rating_numberic"),
                        queryResult.getString("rating_text"),
                        queryResult.getString("username")

                );

                 ratings.add(grdto);
            } while(queryResult.next());
        }

        return ratings;
    }



    public float getAverageRating(int userId) {
        int averageRating=0;
        int count =0;
        try {
            ResultSet queryResult = super.getQuery("SELECT rating_numberic From rating where rated_user_id="+userId);

            if(queryResult.next()) {
                do {
                    averageRating=averageRating+queryResult.getInt("rating_numberic");
                    count++;





                } while(queryResult.next());
            }



        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        averageRating=averageRating/count;


        return averageRating;
    }
}




