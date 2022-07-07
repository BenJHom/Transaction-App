package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;


@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;
    private TransferDao transferDao;

    public TransferController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao, TransferDao transferDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public User[] getUsersForTransfer(@PathVariable long id){
        User[] users = new User[userDao.findAll().size()];
        for (int i = 0; i < userDao.findAll().size(); i++){
            if(!(users[i].getId() == id)) {
                users[i] = userDao.findAll().get(i);
            }
        }
        return users;
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.POST)
    public Transfer sendBucks(@RequestBody Transfer transfer, @PathVariable int id){

        int accountFrom = transferDao.getAccountId(transfer.getSender().getId());
        int accountTo = transferDao.getAccountId(transfer.getReceiverId());
        transfer.setAccountFrom(accountFrom);
        transfer.setAccountTo(accountTo);

        transfer = transferDao.createTransfer(transfer);

        transferDao.doTransfer(transfer);

        return transfer;
    }
}

