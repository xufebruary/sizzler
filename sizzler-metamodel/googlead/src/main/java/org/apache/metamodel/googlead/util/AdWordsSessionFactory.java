package org.apache.metamodel.googlead.util;

import com.google.api.ads.adwords.lib.client.AdWordsSession;
import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.client.auth.oauth2.Credential;
import org.apache.metamodel.util.Oauth2Token;

/**
 * Created by ptmind on 2015/9/22.
 */
public class AdWordsSessionFactory {

  public static AdWordsSession createAdWordsSession(Oauth2Token oauth2Token, String developToken,
      String userAgent) throws Exception {

    return createAdWordsSession(oauth2Token.getClientId(), oauth2Token.getClientSecret(),
        oauth2Token.getRefreshToken(), developToken, userAgent);
  }

  public static AdWordsSession createAdWordsSession(String clientId, String clientSecret,
      String refreshToken, String developerToken, String userAgent) throws Exception {
    // Create a valid OAuth 2.0 credential without using a properties file.
    Credential credential =
        new OfflineCredentials.Builder().forApi(OfflineCredentials.Api.ADWORDS)
            .withClientSecrets(clientId, clientSecret).withRefreshToken(refreshToken).build()
            .generateCredential();

    // Create a new AdWordsSession without using a properties file.
    return new AdWordsSession.Builder().withDeveloperToken(developerToken).withUserAgent(userAgent)
        .withOAuth2Credential(credential).build();
  }



}
