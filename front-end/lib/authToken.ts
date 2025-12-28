// In-memory access token store to reduce reliance on localStorage (mitigates XSS risk)
// This keeps the token in module memory during a session. The refresh token
// should still be stored as HttpOnly cookie by the server.

let _accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  _accessToken = token;
}

export function getAccessToken(): string | null {
  return _accessToken;
}

export function clearAccessToken() {
  _accessToken = null;
}
