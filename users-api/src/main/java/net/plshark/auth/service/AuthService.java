package net.plshark.auth.service;

import net.plshark.auth.model.AccountCredentials;
import net.plshark.auth.model.AuthToken;

public interface AuthService {

    AuthToken generateAuthToken(AccountCredentials credentials);

    AuthToken refreshAuthToken(String refreshToken);
}
