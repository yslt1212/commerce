package de.sep.server.worker;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.adapter.EmailAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.domain.entity.AuctionEntity;
import de.sep.domain.entity.BidEntity;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;

import java.sql.SQLException;
import java.util.List;

public class AuctionConclusionWorker implements Runnable{

    AuctionAdapter auctionAdapter;
    UserAdapter userAdapter;
    EmailAdapter emailAdapter;

    public AuctionConclusionWorker(){
        try {
            auctionAdapter = new AuctionAdapter();
            userAdapter = new UserAdapter();
            emailAdapter = new EmailAdapter();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            List<AuctionEntity> expiredAuctions = auctionAdapter.getUntransferredAuctions();
            for (AuctionEntity auction: expiredAuctions) {
                BidEntity highestBid = auctionAdapter.getHighestBid(auction.getId());
                if(highestBid != null) {
                    notifySeller(auction, highestBid);
                    transferAuctionForHighestBid(auction, highestBid);
                }
            }
        } catch (SQLException | NotFoundException | InternalServerErrorException e) {
            e.printStackTrace();
        }
    }

    void transferAuctionForHighestBid(AuctionEntity auction, BidEntity highestBid) throws NotFoundException, InternalServerErrorException, SQLException {
        double transactionAmount = highestBid.getBidAmount();
        userAdapter.handleTransaction(transactionAmount,highestBid.getBidderId(),auction.getSeller());
        auctionAdapter.orderAuctionForHighestBid(auction, highestBid);
        auctionAdapter.addAuctionTransferred(auction.getId());
        auctionAdapter.deleteBidsForAuctionId(auction.getId());
    }

    void notifySeller(AuctionEntity auction, BidEntity highestBid) throws NotFoundException, InternalServerErrorException {
        UserEntity seller = userAdapter.getUserById(auction.getSeller());
        UserEntity bidder = userAdapter.getUserById(highestBid.getBidderId());
        String subject = String.format("Auction on %s has concluded!!",auction.getName());
        String message = String.format("Hello %s,\n\nyour auction on the item \"%s\" has concluded.\nThe final highest bidder was %s, they auctioned the item for %.2f$EP.\n\nCONGRATULATIONS"
                ,seller.getUsername(),auction.getName(),bidder.getUsername(),highestBid.getBidAmount());
        emailAdapter.sendMail(subject,message,seller.getEmail());
    }
}
