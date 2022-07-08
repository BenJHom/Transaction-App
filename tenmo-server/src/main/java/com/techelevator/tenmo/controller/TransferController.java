package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        List<User> userList = userDao.findAll();
        boolean foundUser = false;
        User[] users = new User[userList.size()-1];
        for (int i = 0; i < userList.size(); i++){
            if((userList.get(i).getId() == id)) {
                foundUser = true;
            }else if (foundUser){
                users[i-1] = userList.get(i);
            }else{
                users[i] = userList.get(i);
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
        transfer.setType(2);

        transferDao.doTransfer(transfer);

        transfer = transferDao.createTransfer(transfer);

        return transfer;
    }

    @RequestMapping(path = "/history/{id}", method = RequestMethod.GET)
    public Transfer[] listTransfers(@PathVariable int id){
        List<Transfer> transferList = transferDao.listTransfers(id);
        Transfer[] transferArray = new Transfer[transferList.size()];
        for (int i = 0; i < transferList.size() ; i++){
            transferArray[i] = transferList.get(i);
        }
        return transferArray;
    }
}

