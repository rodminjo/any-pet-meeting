package com.backend.accountmanagement.account.service.port;

import com.backend.accountmanagement.account.domain.AccountSNS;
import com.backend.accountmanagement.account.domain.SNSProvider;
import java.util.Optional;

public interface AccountSNSRepository {


  Optional<AccountSNS> findByEmailAndProvider(String email, SNSProvider provider);

  AccountSNS save(AccountSNS accountSNSDomain);
}
