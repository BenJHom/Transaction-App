package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

Transfer[] getTransfers();

Transfer createTransfer(Transfer transfer);

int getAccountId(long userId);

boolean doTransfer(Transfer transfer);

boolean approveTransfer(Transfer transfer);
}
