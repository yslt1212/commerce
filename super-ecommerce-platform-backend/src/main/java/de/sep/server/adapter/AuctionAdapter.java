package de.sep.server.adapter;


import de.sep.domain.dto.GetHighestBidderDto;
import de.sep.domain.dto.AddAuctionDto;
import de.sep.domain.dto.GetAuctionDto;
import de.sep.domain.entity.AuctionEntity;
import de.sep.domain.entity.BidEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.util.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class AuctionAdapter extends DataAdapter {
    Logger logger = new Logger(getClass().getCanonicalName());
    private String notFoundByIdMessage = "Wir konnten keine Auktion mit dieser Id finden";

    public AuctionAdapter() throws SQLException {
        super("auctionview");
        super.setIdCol("auction_id");
        super.setNameCol("offername");
    }

    public List<GetHighestBidderDto> getHighestBidder() throws NotFoundException {
        List<GetHighestBidderDto> hbs = new ArrayList<>();
        int id = -100;
        try {
            ResultSet queryResult = super.getQuery("SELECT *FROM auction LEFT JOIN bid ON bid.auction_id = auction.auction_id ORDER BY auction.auction_id ASC, bid.bid_amount DESC");
            GetHighestBidderDto hb = null;
            if(queryResult.next()) {
                id = queryResult.getInt("auction_id");
                hb = new GetHighestBidderDto(
                        queryResult.getInt("auction_id"),
                        queryResult.getInt("user_id"));
                        hbs.add(hb);
                        queryResult.next();
                do {
                    if (id != queryResult.getInt("auction_id")){
                        hb = new GetHighestBidderDto(
                                queryResult.getInt("auction_id"),
                                queryResult.getInt("user_id"));
                        hbs.add(hb);
                        id = queryResult.getInt("auction_id");
                    }
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return hbs;
    }





    public List<GetAuctionDto> getAllAuctions() throws NotFoundException {
        List<GetAuctionDto> auctions = new ArrayList<>();

        try {
            ResultSet queryResult = super.getQuery("SELECT * FROM "+this.table+" WHERE end_date >= now()");
            GetAuctionDto auction = null;
            if(queryResult.next()) {
                do {
                    auction = new GetAuctionDto(
                            queryResult.getInt("auction_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getTimestamp("end_date"),
                            queryResult.getDouble("start_price"),
                            queryResult.getDouble("bid_amount"),
                            queryResult.getInt("delivery_type"));
                    auctions.add(auction);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return auctions;
    }


    public BidEntity getHighestBid(int auctionId) throws SQLException {
        String query = String.format("SELECT * FROM bid WHERE auction_id = %d ORDER BY bid_amount DESC LIMIT 1",auctionId);
        ResultSet resultSet = getQuery(query);
        BidEntity highestBid = null;
        if(resultSet.next()){
           highestBid = new BidEntity(resultSet.getInt("bid_id"),resultSet.getInt("auction_id"),resultSet.getInt("user_id"),resultSet.getDouble("bid_amount"));
           return highestBid;
        }else {
            System.err.println("Auction ended but nobody has bid");
        }
        return highestBid;
    }


    public List<GetAuctionDto> getAllExpiredAuctions() throws NotFoundException {
        List<GetAuctionDto> auctions = new ArrayList<>();
        try {
            ResultSet queryResult = super.getQuery("SELECT * FROM "+this.table+" WHERE end_date< now()");
            GetAuctionDto auction = null;
            if(queryResult.next()) {
                do {
                    auction = new GetAuctionDto(
                            queryResult.getInt("auction_id"),
                            queryResult.getString("offername"),
                            queryResult.getInt("seller"),
                            queryResult.getString("category"),
                            queryResult.getString("description"),
                            queryResult.getTimestamp("end_date"),
                            queryResult.getDouble("start_price"),
                            queryResult.getDouble("bid_amount"),
                            queryResult.getInt("delivery_type"));
                    auctions.add(auction);
                } while (queryResult.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return auctions;
    }

    public void deleteBidsForAuctionId(int auctionId) throws SQLException {
        // Delete all the associated bid's of the auction
        String deleteBidsQuery = String.format("DELETE FROM bid WHERE auction_id = %d",auctionId);

        PreparedStatement preparedStatement = connection.prepareStatement(deleteBidsQuery);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void addAuctionTransferred(int auctionId) throws SQLException {
        // Insert the auction into the auction_transfer table so we don't transfer it again
        String query = String.format("INSERT INTO auction_transfer (auction_id) VALUES (%d)",auctionId);
        addQuery(query);
    }

    public void orderAuctionForHighestBid(AuctionEntity auction, BidEntity highestBid) throws SQLException {
        // Calculate delivery date
        Random random = new Random();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp deliveryTimestamp = new Timestamp(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * (random.nextInt(7)+3));
        // Get the offerId
        int offerId = 0;
        String b64Image = "";
        int deliveryType = 0;
        ResultSet offerResultSet = getQuery(String.format("SELECT offer_id,img,delivery_type FROM auctionview where auction_id=%d",auction.getId()));
        if(offerResultSet.next()){
            offerId = offerResultSet.getInt("offer_id");
            b64Image = offerResultSet.getString("img");
            deliveryType = offerResultSet.getInt("delivery_type");
        }
        // Insert into the offer_history
        int offer_history_id = addQuery(String.format("INSERT INTO offer_history (offer_id, offername, seller, category, description, img, delivery_type) VALUES ('%d', '%s', '%d', '%s', '%s', '%s', '%d')",
                offerId,auction.getName(),auction.getSeller(),auction.getCategory(),auction.getDescription(),b64Image,deliveryType));

        // Insert the current Auction values into an auction_history
        addQuery(String.format(Locale.US,
                "Insert into auction_history (offer_history_id, start_price, end_date) VALUES ('%d', '%s', '%s')",
                offer_history_id, highestBid.getBidAmount(),auction.getEndDate().toString()));
        // Insert an order
        addQuery(String.format("INSERT INTO orders (offer_history_id, user_id, delivery_date) VALUES ('%d', '%d', '%s')",
                offer_history_id,highestBid.getBidderId(), deliveryTimestamp.toString()));
    }

    public List<AuctionEntity> getUntransferredAuctions() throws SQLException {
        String query = "SELECT * FROM auctionview WHERE auction_id IN (SELECT auct.auction_id FROM auction auct LEFT JOIN auction_transfer transfer ON auct.auction_id = transfer.auction_id WHERE transfer.auction_id IS NULL) AND end_date < now()";
        System.out.println(query);
        List<AuctionEntity> auctions = new ArrayList<>();
        ResultSet resultSet = getQuery(query);

        while(resultSet.next()){
            AuctionEntity auction = new AuctionEntity(
                    resultSet.getInt("auction_id"),
                    resultSet.getString("offername"),
                    resultSet.getInt("seller"),
                    resultSet.getString("category"),
                    resultSet.getString("description"),
                    resultSet.getTimestamp("end_date"),
                    resultSet.getDouble("start_price"),
                    resultSet.getDouble("bid_amount")
                    );
            auctions.add(auction);
        }
        resultSet.close();
        return auctions;
    }

    public GetAuctionDto getAuctionById(int id) throws NotFoundException, InternalServerErrorException {
        ResultSet rs = null;

        GetAuctionDto auction = null;
        try {

            rs = super.getById(id);

            if(rs.next()) {
                do {
                    auction = new GetAuctionDto(
                            rs.getInt("auction_id"),
                            rs.getString("offername"),
                            rs.getInt("seller"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getTimestamp("end_date"),
                            rs.getDouble("start_price"),
                            rs.getDouble("bid_amount"),
                            rs.getInt("delivery_type"));
                } while (rs.next());
            }else{
                throw new NotFoundException(notFoundByIdMessage);
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return auction;
    }

    public void addAuctions(List<AddAuctionDto> auctions) {
            String intoOfferBaseQuery = "INSERT INTO OFFER (offername, seller,category, description, img, delivery_type) VALUES";
            StringBuilder queryBuilder = new StringBuilder(intoOfferBaseQuery);
            try {
                for(AddAuctionDto addAuctionDto: auctions) {
                    queryBuilder.append(String.format(Locale.US,"('%s','%d','%s','%s','%s','%d')",
                            addAuctionDto.getOffername(),
                            addAuctionDto.getSeller(),
                            "",
                            addAuctionDto.getDescription(),
                            addAuctionDto.getImg(),
                            addAuctionDto.getDeliveryType()));
                    int offerid = addQuery(queryBuilder.toString());
                    String query = String.format("INSERT INTO AUCTION (offer_id, end_date, start_price) VALUES('%d', '%s', '%s')",
                            offerid,
                            addAuctionDto.getEndDate(),
                            Double.toString(addAuctionDto.getStartPrice()));
                    addQuery(query);
                    queryBuilder = new StringBuilder(intoOfferBaseQuery);
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }

    public String getImg(int auctionId) throws NotFoundException, InternalServerErrorException {


        try {
            String query = "Select img from auctionview where auction_id=" + auctionId;
            ResultSet resultSet = super.getQuery(query);

            if(!resultSet.next()) {
                throw new NotFoundException("Wir konnten keine auction mit dieser id finden");
            } else {
                String img = "";
                do {
                    img = resultSet.getString("img");
                }while (resultSet.next());
                return img;
            }

        } catch (SQLException e) {
            throw new InternalServerErrorException(e.getMessage());
        }


    }
}
