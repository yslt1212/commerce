package de.sep.server.adapter;

import de.sep.domain.entity.AuctionEntity;
import de.sep.domain.entity.BidEntity;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import de.sep.server.worker.AuctionConclusionWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.internal.matchers.Not;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class DoubleAuthAdapterTest {

    DoubleAuthAdapter doubleAuthAdapter;
    String email = "yannick.dohmen@test.de";
    String definitlyNotInDb = "dasfhgdfsdadgfsdgfhgd";


    private int emailCode;

    @Before
    public void setUp(){
        doubleAuthAdapter = spy(DoubleAuthAdapter.class);


    }

    @Test
    public void getCodeEmailTest() throws SQLException, NotFoundException, InternalServerErrorException {
        int emailCodeId = doubleAuthAdapter.createCode(email, 0);
        emailCode = doubleAuthAdapter.getCodeById(emailCodeId);
        Assert.assertTrue(doubleAuthAdapter.getCode(email, 0) == emailCode);
    }

    @Test
    public void getCodeTelTest() throws SQLException, NotFoundException, InternalServerErrorException {
        int telCodeId = doubleAuthAdapter.createCode("test12", 1);
        int telCode = doubleAuthAdapter.getCodeById(telCodeId);
        Assert.assertTrue(doubleAuthAdapter.getCode("test12", 1) == telCode);
    }

    @Test(expected = NotFoundException.class)
    public void getCodeEmailNotFoundExceptionTest() throws SQLException, NotFoundException, InternalServerErrorException {
        doubleAuthAdapter.getCode(definitlyNotInDb, 0);
    }

    @Test(expected = NotFoundException.class)
    public void getCodeTelNotFoundExceptionTest() throws SQLException, NotFoundException, InternalServerErrorException {
        doubleAuthAdapter.getCode(definitlyNotInDb, 1);
    }

    @Test(expected = NotFoundException.class)
    public void deactivateCodeEmailTest() throws SQLException, NotFoundException {
            doubleAuthAdapter.deactivateCode(emailCode, 0);
            doubleAuthAdapter.getCode(email,0);
    }

}
