package de.sep.server.adapter;

import de.sep.domain.dto.RegisterUserDto;
import de.sep.domain.entity.UserEntity;
import de.sep.server.errors.ForbiddenException;
import de.sep.server.errors.InternalServerErrorException;
import de.sep.server.errors.NotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserAdapterTest {

    UserAdapter userAdapter;
    UserEntity testPrivatUser;
    UserEntity testCommercialUser;
    String companyName = "TestUser1Company";

    @Rule
    public final ExpectedException notFoundException = ExpectedException.none();

    @Rule
    public final ExpectedException internalServierErrorException = ExpectedException.none();

    @Rule
    public final ExpectedException unauthorizedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        userAdapter = new UserAdapter();
        RegisterUserDto testPrivatUserRegisterDto = new RegisterUserDto("TestPrivatUser1", "password", "test@test.de", "Oskarstraße 1","46149", "Oberhausen", 0, "", "", 0);
        RegisterUserDto testCommercialUserRegisterDto = new RegisterUserDto("TestCommercialUser1", "password", "test@test.de", "Oskarstraße 1","46149", "Oberhausen", 0, "", companyName, 1);
        try {
            userAdapter.registerUser(testCommercialUserRegisterDto);
            userAdapter.registerUser(testPrivatUserRegisterDto);
        } catch (Exception e) {

        }

        testPrivatUser = userAdapter.getByName("TestPrivatUser1");
        testCommercialUser = userAdapter.getByName("TestCommercialUser1");
    }



    @Test
    public void getUserByIdTest() throws NotFoundException, InternalServerErrorException, SQLException {
        UserEntity foundUser = userAdapter.getUserById(testPrivatUser.getId());
        assertEquals(foundUser, testPrivatUser);
    }

    @Test
    public void searchUserByNameTest() throws NotFoundException, InternalServerErrorException, SQLException {
        UserEntity foundUser = userAdapter.searchByName("TestPrivat");
        assertEquals(foundUser, testPrivatUser);
    }

    @Test
    public void getCompanyNameByIdTest() throws NotFoundException, InternalServerErrorException, SQLException {
        String companyname = userAdapter.getCompanynameByID(testCommercialUser.getId());
        assertEquals(companyname, this.companyName);
    }

    @Test
    public void loginUserWrongPasswordTest() throws NotFoundException, InternalServerErrorException, SQLException, ForbiddenException {
        unauthorizedException.expect(ForbiddenException.class);
        unauthorizedException.expectMessage("Wrong");
        userAdapter.loginUser(testPrivatUser.getUsername(), "assdfg");

    }

    @Test
    public void loginUserWrongUsernameTest() throws NotFoundException, InternalServerErrorException, SQLException, ForbiddenException {
        notFoundException.expectMessage("keinen Nutzer");
        userAdapter.loginUser("asdf", testPrivatUser.getPassword());
    }


}
