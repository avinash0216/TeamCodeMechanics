1. "alg": "RS256"
2. "kid": "c3c703b1-ef95-4e82-97a7-d0bc194cb6c8"
3. Asymmetric encryption approach means every client have their own individual private key. Symmetric encryption approach means all clients use the same key which is a risk.
4. sub is the clientid of the service "bank-service" in case of client credentials flow. In case of authorization code flow, sub is the userid of the user who logged in.
5. iss is the issuer of the token, which is the authorization server that issued the token. "exp" is the expiration time of the token, which is a timestamp indicating when the token will expire and no longer be valid. "iat" is the issued at time, which is a timestamp indicating when the token was issued. "nbf" is the not before time, which is a timestamp indicating when the token will become valid. "jti" is a unique identifier for the token.
6. "aud" is the audience of the token, which is the resource server that the token is intended for. "iss" is the issuer of the token, which is the authorization server that issued the token.
7. token life time is 60 min
8. "transaction.read",
   "account.read"
9. roles, preferred_username and name are not present since this is using client credential approach. There is no user incolved here.
10. Pasting value of n in jwt.io and clicking on "decode" will give you the public key which can be used to verify the signature of the token. The public key is used to verify that the token was indeed signed by the private key corresponding to the "kid" in the header.
11. The new token will not verify with the public key copied earlier. Restarting the authorization server rotated or replaced the signing key pair. The new token was signed with the new private key, so the old public key does not match and verification fails. The token header kid will point to the new key.
12. 