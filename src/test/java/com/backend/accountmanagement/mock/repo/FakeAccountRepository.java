package com.backend.accountmanagement.mock.repo;

import com.backend.accountmanagement.account.domain.Account;
import com.backend.accountmanagement.account.service.port.AccountRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Setter;

@Setter
public class FakeAccountRepository implements AccountRepository {

  private final AtomicLong autoGeneratedId = new AtomicLong(0);
  private final List<Account> data = Collections.synchronizedList(new ArrayList<>());

  @Override
  public Optional<Account> findByEmail(String email) {
    return data.stream()
        .filter(account -> account.getEmail().equals(email))
        .findFirst();

  }


  @Override
  public boolean existByEmail(String email) {
    for (Account account : data){
      if (account.getEmail().equals(email)){
        return true;
      }
    }
    return false;

  }

  @Override
  public Account save(Account joinAccount) {
    if (joinAccount.getId() == 0) {
      Account newUser = Account.builder()
          .id(autoGeneratedId.incrementAndGet())
          .name(joinAccount.getName())
          .email(joinAccount.getEmail())
          .password(joinAccount.getPassword())
          .build();
      newUser.getRoleSet().addAll(joinAccount.getRoleSet());

      data.add(newUser);
      return newUser;

    } else {
      data.removeIf(item -> Objects.equals(item.getId(), joinAccount.getId()));
      data.add(joinAccount);
      return joinAccount;

    }
  }
}
