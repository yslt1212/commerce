package de.sep.server.worker;

import de.sep.server.adapter.AuctionAdapter;
import de.sep.server.adapter.EmailAdapter;
import de.sep.server.adapter.UserAdapter;
import de.sep.domain.entity.AuctionEntity;
import de.sep.domain.entity.BidEntity;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class AuctionConclusionWorkerTest {

    AuctionConclusionWorker auctionConclusionWorker;

    @Mock
    EmailAdapter emailAdapter;

    @Mock
    UserAdapter userAdapter;

    @Mock
    AuctionAdapter auctionAdapter;

    @Before
    public void setUp(){
        emailAdapter = mock(EmailAdapter.class);
        userAdapter = mock(UserAdapter.class);
        auctionAdapter = mock(AuctionAdapter.class);
        auctionConclusionWorker = spy(AuctionConclusionWorker.class);
        auctionConclusionWorker.userAdapter = userAdapter;
        auctionConclusionWorker.auctionAdapter = auctionAdapter;
        auctionConclusionWorker.emailAdapter = emailAdapter;
    }

    @Test
    public void notifyOnAuctionConcludedTest() throws SQLException, NotFoundException, InternalServerErrorException {
        UserEntity seller = UserEntity.builder()
                .id(3)
                .username("testseller")
                .email("testseller@testmail.de")
                .type(0).build();
        UserEntity bidder = UserEntity.builder()
                .id(4)
                .username("testbidder")
                .balance(10)
                .build();
        AuctionEntity untransferredAuction = new AuctionEntity(1,"testauctionname",seller.getId(),"test","test",new Timestamp(0),8.0,10.0);
        List<AuctionEntity> untransferredAuctions = new ArrayList<>();
        untransferredAuctions.add(untransferredAuction);

        BidEntity highestBid = new BidEntity(1,untransferredAuction.getId(),bidder.getId(),10);

        when(auctionAdapter.getUntransferredAuctions()).thenReturn(untransferredAuctions);
        when(auctionAdapter.getHighestBid(eq(highestBid.getId()))).thenReturn(highestBid);

        when(userAdapter.getUserById(eq(seller.getId()))).thenReturn(seller);
        when(userAdapter.getUserById(eq(bidder.getId()))).thenReturn(bidder);

        doNothing().when(auctionConclusionWorker).transferAuctionForHighestBid(any(),any());

        auctionConclusionWorker.run();

        verify(auctionConclusionWorker).notifySeller(untransferredAuction,highestBid);
        verify(emailAdapter).sendMail(
                contains(untransferredAuction.getName()),
                contains(bidder.getUsername()),
                contains(seller.getEmail()));
    }

}