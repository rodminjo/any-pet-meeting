package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.Account;
import java.util.Optional;

public interface AccountRepository {

  public Optional<Account> findByEmail(String email);
  public boolean existByEmail(String email);
  public Account save(Account joinAccount);
}
